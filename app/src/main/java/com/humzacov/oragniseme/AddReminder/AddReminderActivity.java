package com.humzacov.oragniseme.AddReminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.humzacov.oragniseme.AddReminder.DateTime.DateDialogFragment;
import com.humzacov.oragniseme.R;
import com.humzacov.oragniseme.databinding.ActivityAddReminderBinding;

public class AddReminderActivity extends AppCompatActivity {
    private AddReminderPresenter addReminderPresenter; //stores presenter
    private ActivityAddReminderBinding binding; //store viewbinding object
    private boolean locationPermissionAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view binding object, so no need for findViewByID()
        ActivityAddReminderBinding binding = ActivityAddReminderBinding.inflate(getLayoutInflater()); //set binding object

        //receive user login data via intent
        Bundle extras = getIntent().getExtras();
        GoogleSignInAccount gsa = getIntent().getParcelableExtra("user");

        //instantiate presenter
        FragmentManager fm = getSupportFragmentManager();
        addReminderPresenter = new AddReminderPresenter(this, binding, fm, gsa);

        //set view listeners
        binding.saveButton.setOnClickListener(addReminderPresenter.saveButtonListener);
        binding.cameraButton.setOnClickListener(addReminderPresenter.cameraButtonListener);
        binding.audioButton.setOnClickListener(addReminderPresenter.micButtonListener);
        binding.timeButton.setOnClickListener(addReminderPresenter.timeButtonListener);
        binding.locationButton.setOnClickListener(addReminderPresenter.locButtonListener);

        //set content view
        setContentView(binding.getRoot());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //call result handler from presenter, so as to move all functionality there
        addReminderPresenter.onActivityResult(requestCode, resultCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //method called when requesting permission to access location
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (locationPermissionAccepted ) addReminderPresenter.locationTask();
    }
}