package com.humzacov.oragniseme.AddReminder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.humzacov.oragniseme.AddReminder.DateTime.DateDialogFragment;
import com.humzacov.oragniseme.AddReminder.DateTime.TimeDialogFragment;
import com.humzacov.oragniseme.AddReminder.Microphone.MicrophoneActivity;
import com.humzacov.oragniseme.databinding.ActivityAddReminderBinding;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.app.Activity.RESULT_OK;

public class AddReminderPresenter {

    private final Activity addReminderActivity; //reference to activity that instantiated presenter
    private final ActivityAddReminderBinding binding; //reference to UI components

    private final FirebaseDatabase database; //reference to database
    private final FirebaseStorage storage; //reference to cloud storage

    //request codes for intents (onActivityResult)
    private final int REQUEST_PHOTO_CODE = 1;
    private final int REQUEST_SPEECH_CODE = 2;
    private final int REQUEST_LOCATION_CODE = 3;

    private final FragmentManager fm; //fragment manager belonging to acivity
    private final GoogleSignInAccount gsa;

    // variables for photo and speech files. date/time, and location
    private Bitmap photo = null;
    private File speech = null;
    private String time = "";
    private String date = "";
    private GoogleMap map;
    private String location = "";

    // boolean files for whether async photo/speech upload completed
    private Boolean photoDone = false;
    private Boolean speechDone = false;

    public AddReminderPresenter(Activity addReminderActivity, ActivityAddReminderBinding binding,
                                FragmentManager fm, GoogleSignInAccount gsa) {
        this.addReminderActivity = addReminderActivity;
        this.binding = binding;
        this.fm = fm;
        this.database = FirebaseDatabase.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.gsa = gsa;
    }

    private void saveReminder() {
        //create new reminder object
        Reminder reminder = new Reminder();

        //get reference to write to database
        DatabaseReference reminderRef = database.getReference("reminder");

        //generate object with unique key (push) and write values to database
        DatabaseReference pushReminder = reminderRef.push();
        String key = pushReminder.getKey(); //unique object key

        //set values to reminder
        reminder.setTitle(binding.title.getText().toString());
        reminder.setDescription(binding.description.getText().toString());
        reminder.setPrivacySettings(binding.privacySpinner.getSelectedItem().toString());
        reminder.setRepeatSettings(binding.repeatSpinner.getSelectedItem().toString());
        reminder.setLocation(location);
        reminder.setTime(timeToUTC());
        reminder.setOwner(gsa.getEmail());

        //call async functions to upload photo and speech
        //and attach link to database
        storePhoto(key, reminder); //upload photo
        try {
            storeAudio(key, reminder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //write created object to database
        pushReminder.setValue(reminder);

        finishActivity(); //finish activity if all has been uploaded
    }

    protected String timeToUTC() {
        //if either date or time unfilled
        if (!this.date.equals("") | !this.time.equals("")) {
            //parse date string to LocalDateTime
            String time = this.date + "T" + this.time;
            LocalDateTime localTime = LocalDateTime.parse(time);

            //attach current time zone information
            ZonedDateTime zoneTime = localTime.atZone(ZoneId.systemDefault());
            //convert to utc
            ZonedDateTime utcTime = zoneTime.withZoneSameInstant(ZoneId.of("UTC"));
            return utcTime.toString();
        }
        return ""; //return empty string if no data filled
    }

    private void openCamera() {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        addReminderActivity.startActivityForResult(photoIntent, REQUEST_PHOTO_CODE);
    }

    private void storePhoto(String key, Reminder reminder) {
        //if user has taken photo, save to cloud storage

        //create references to where file should be stored
        StorageReference storageRef = storage.getReference();
        StorageReference photoRef = storageRef.child("photos/" + key + ".jpg");

        if (this.photo != null) {
            /* prepare upload process */
            //convert photo to input stream for lower memory usage
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

            //start async task to upload photo
            UploadTask uploadTask = photoRef.putStream(inputStream);
            linkFileToDB(uploadTask, photoRef, key, "photo");
        }
    }

    private void linkFileToDB(UploadTask uploadTask, StorageReference fileRef, String key, String childName) {
        /* async code for uploading file, and
         * then saving link to reminder object in db once done. */
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //return download link once file has been uploaded
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    //task to upload to cloud storage is complete; store file link in variable
                    Uri downloadUri = task.getResult();

                    //save link to file in database
                    saveValue(key, childName, downloadUri.toString());

                    updateProgress(childName); //update variables relevant to finishActivity function
                    finishActivity(); //finish activity if all files uploaded
                }
            }
        });
    }

    private void saveValue(String key, String childName, String value) {
        /* function to save link to file in database */

        //create database reference (where to store file in database)
        DatabaseReference reminderRef = database.getReference("reminder").child(key);
        DatabaseReference dbFileRef = reminderRef.child(childName);

        //finally, save link to db
        dbFileRef.setValue(value);
    }

    private void storeAudio(String key, Reminder reminder) throws IOException {
        //if user has recorded speech, save to cloud storage
        if (speech != null) {
            //create references to where file should be stored
            StorageReference storageRef = storage.getReference();
            StorageReference audioRef = storageRef.child("recordings/" + key + ".3gp");

            /* prepare upload process */
            //convert speech to input stream for lower memory usage
            byte[] speechBytes = Files.readAllBytes(Paths.get(speech.toURI())); //convert to byte array
            InputStream inputStream = new ByteArrayInputStream(speechBytes); //convert to input stream

            //start async task to upload photo
            UploadTask uploadTask = audioRef.putStream(inputStream);
            linkFileToDB(uploadTask, audioRef, key, "recordings");
        }
    }

    private void startRecordActivity() {
        //create intent to launch microphone-recording activity
        Intent intent = new Intent(addReminderActivity, MicrophoneActivity.class);
        addReminderActivity.startActivityForResult(intent, REQUEST_SPEECH_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO_CODE && resultCode == RESULT_OK) {
            //if photo is successful, retrieve image data from intent
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            this.photo = photo; //store photo in class

            //update image button to show that photo has been taken
            binding.cameraButton.setImageBitmap(photo);
        } else if (requestCode == REQUEST_SPEECH_CODE && resultCode == RESULT_OK) {
            //if recording is successful, retrieve recording from intent
            Bundle extras = data.getExtras();
            this.speech = (File) extras.get("audio");
        }
    }

    private void updateProgress(String fileType) {
        if (fileType.equals("recordings")) {
            speechDone = true;
        } else if (fileType.equals("photo")) {
            photoDone = true;
        }
    }

    private void finishActivity() {
        //if all async tasks are done, finish activity

        if (photoDone && speechDone) {
            addReminderActivity.finish(); //finish if uploaded speech and photo
        } else if (photo == null && speechDone) {
            addReminderActivity.finish(); //finish if only chose to upload speech
        } else if (speech == null && photoDone) {
            addReminderActivity.finish(); //finish if only chose to upload photo
        } else if (speech == null && photo == null) {
            addReminderActivity.finish(); //finish if chose to upload no files
        }
    }

    private void timeButton() {
        dateFragment(); //show date
    }

    private void dateFragment() {
        DialogFragment dateFrag = new DateDialogFragment(this);
        dateFrag.show(fm, "Select Date");
    }

    public void setDate(int year, int month, int dayOfMonth) {
        String strMonth = formatNum(month, 'm'); //format month as required by parser
        String strDay = formatNum(dayOfMonth, 'd');
        //concatenate individual elements to format
        this.date = year + "-" + strMonth + "-" + strDay;

        //once date set, show time fragment
        timeFragment();
    }

    private void timeFragment() {
        DialogFragment timeFrag = new TimeDialogFragment(this);
        timeFrag.show(fm, "Select Time");
    }

    private String formatNum(int num, char dateType) {
        //if type is 'm' (short for 'month')
        if (dateType == 'm') {
            num += 1; //increment by 1 to change month from 0-index to 1-index
        }

        String strNum = String.valueOf(num); //convert to string

        /* parser requires YYYY-MM-DD format,
         * so place 0 in front if day or month
         * only has one digit. */
        if (strNum.length() == 1) {
            strNum = "0" + strNum;
        }

        return strNum;
    }

    public void setTime(int hourOfDay, int minute) {
        //format variables so they can be parsed
        String strHour = formatNum(hourOfDay, 'h');
        String strMin = formatNum(minute, 't');

        //edit variable to point to specified hour and minute
        this.time = strHour + ":" + strMin + ":00";
    }

    private void locationButton() {
        //request permission to access location
        ActivityCompat.requestPermissions(addReminderActivity
                , new String[]{ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_CODE);
    }

    protected void locationTask() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(addReminderActivity);
        if (ActivityCompat.checkSelfPermission(addReminderActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(addReminderActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Location> task) {
                    Location curLocation = task.getResult();
                    location = curLocation.toString();
                }
            });
        }
    }

    protected View.OnClickListener saveButtonListener = v -> saveReminder();
    protected View.OnClickListener cameraButtonListener = v -> openCamera();
    protected View.OnClickListener micButtonListener = v -> startRecordActivity();
    protected View.OnClickListener timeButtonListener = v -> timeButton();
    protected View.OnClickListener locButtonListener = v -> locationButton();
}