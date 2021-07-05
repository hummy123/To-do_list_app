package com.humzacov.oragniseme.Host;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.humzacov.oragniseme.R;
import com.humzacov.oragniseme.databinding.ActivityHostBinding;

public class HostActivity extends AppCompatActivity {

    private HostPresenter hostPresenter; //stores presenter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view binding object, so no need for findViewByID()
        ActivityHostBinding binding = ActivityHostBinding.inflate(getLayoutInflater()); //set binding object

        //receive user login data via intent
        Bundle extras = getIntent().getExtras();
        GoogleSignInAccount gsa = getIntent().getParcelableExtra("user");

        //instantiate presenter
        hostPresenter = new HostPresenter(this, binding, gsa);

        //binding.reminderList

        //set listeners
        binding.newReminderButton.setOnClickListener(hostPresenter.reminderButtonListener);
        binding.toggleButton.setOnClickListener(hostPresenter.toggleButtonListener);

        setContentView(binding.getRoot());
    }
}