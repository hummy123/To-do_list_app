package com.humzacov.oragniseme.Host;

import android.content.Intent;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.humzacov.oragniseme.AddReminder.AddReminderActivity;
import com.humzacov.oragniseme.AddReminder.Reminder;
import com.humzacov.oragniseme.databinding.ActivityHostBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HostPresenter {

    private final GoogleSignInAccount gsa;
    private final HostActivity activity;
    private final ActivityHostBinding binding;

    //ArrayList of reminder objects
    private ArrayList<Reminder> reminderList = new ArrayList<Reminder>();
    //store key so reminder can be identified
    private ArrayList<String> keyList = new ArrayList<String>();

    /* boolean variable indicating whether the
    * screen shows user's reminders or public reminders*/
    private boolean publicReminders = true;
    private final FirebaseDatabase database;

    public HostPresenter(HostActivity hostActivity, ActivityHostBinding binding, GoogleSignInAccount gsa) {
        this.gsa = gsa;
        this.activity = hostActivity;
        this.binding = binding;

        this.database = FirebaseDatabase.getInstance();
        readOnStart(); //read data from database
    }

    private void readOnStart() {
        //enable offline persistence, so data can be accessed if user offline
        database.setPersistenceEnabled(true);
        getReminders(); //get reminders
    }

    private void newReminder() {
        Intent intent = new Intent(activity, AddReminderActivity.class);
        intent.putExtra("user", this.gsa);
        activity.startActivity(intent);
    }

    private void listenForData(Query reminderQuery) {
        clearList();
        //get values of reminder objects and perform appropriate actions
        reminderQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                //get Reminder object + identification key
                Pair<Reminder, String> pair = snapshotToReminder(snapshot);
                //add reminder to list and update UI
                addReminder(pair.first, pair.second);
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                //get Reminder object + identification key
                Pair<Reminder, String> pair = snapshotToReminder(snapshot);
                //change reminder in list and update UI
                updateReminder(pair.first, pair.second);
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                //get Reminder object to be removed + identification key
                Pair<Reminder, String> pair = snapshotToReminder(snapshot);
                //remove object from list and update UI
                removeReminder(pair.first, pair.second);
            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private <key, reminder> Pair<Reminder, String> snapshotToReminder(DataSnapshot snapshot) {
        /* function to convert DataSnapshot to Reminder object,
         * and return Reminder object + identification key*/

        //read/create instance of new reminder object
        Reminder reminder = snapshot.getValue(Reminder.class);
        //get unique identification key
        String key = snapshot.getKey();
        //return as pair object
        return new Pair<Reminder, String>(reminder, key);
    }

    private void addReminder(Reminder reminder, String key) {
        /* function to add a child to reminderList */

        reminderList.add(reminder); //add reminder to list
        keyList.add(key); //add identification key for each reminder to lis
        updateList(); //update ui to show new list
    }

    private void updateReminder(Reminder reminder, String key) {
        /* function to update a child in reminderList */
        int changeLoc = identifyReminder(key); //get location of modified reminder
        reminderList.set(changeLoc, reminder); //update modified reminder
        updateList(); //update ui to show new list
    }

    private void removeReminder(Reminder reminder, String key) {
        /* function to remove reminder from reminderList */
        int removeLoc = identifyReminder(key); //get location of removed reminder
        if (reminderList.size() > 0) {
            reminderList.remove(removeLoc); //remove from list
        }
        updateList(); //update ui
    }

    private int identifyReminder(String key) {
        //simple for loop search to identify changed reminder
        int counter = 0;
        //only search if lis not empty; otherwise, return 0
        if (!keyList.isEmpty()) {
            for (String listKey : keyList) {
                if (key.equals(listKey)) {
                    break;
                }
                counter += 1;
            }
        }
        return counter;
    }

    private void getReminders() {
        /* method retrieves data from database and
         * queries for reminders according to criterion */

        //path to reminders in database
        DatabaseReference reminderRef = this.database.getReference("reminder");

        //return query (which retrieves reminders belonging to the user)
        Query reminderQuery = toggleReminders(reminderRef);

        clearList(); //clear list because starting from scratch

        //call event listener to listen for data retrieved from query
        listenForData(reminderQuery);
    }

    private void clearList() {
        this.reminderList.clear(); //list clear so no duplicate data shown
        this.keyList.clear(); //clear corresponding key list identifying each reminder
    }

    private void updateList() {
        /* this method updates the RecyclerView UI element. */

        ReminderAdapter adapter = new ReminderAdapter(this.activity, this.reminderList, this.binding);
        binding.reminderList.setAdapter(adapter);
        binding.reminderList.setLayoutManager(new LinearLayoutManager(this.activity));
        binding.floatingLayout.bringToFront(); //put nav buttons in front of RecyclerView
    }

    private Query toggleReminders(DatabaseReference reminderRef) {
        /* toggles between public reminders and user's own,
         * displaying the desired type in the recyclerview. */

        //invert boolean to work as toggle
        this.publicReminders = (!this.publicReminders);

        if (this.publicReminders) {
            //return list of public reminders if true
            return reminderRef.orderByChild("privacySettings").equalTo("Public");
        } else {
            //return user's own reminders if false
            return reminderRef.orderByChild("owner").equalTo(this.gsa.getEmail());
        }
    }

    private void toggleButton() {
        /* switches reminder query
         * (user's reminders or public reminders0 */
        getReminders();
    }

    protected View.OnClickListener reminderButtonListener = v -> newReminder();
    protected View.OnClickListener toggleButtonListener = v -> toggleButton();
}
