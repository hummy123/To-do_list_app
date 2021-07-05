package com.humzacov.oragniseme.AddReminder.Microphone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.humzacov.oragniseme.databinding.ActivityMicrophoneBinding;

public class MicrophoneActivity extends AppCompatActivity {
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view binding object, so no need for findViewByID()
        com.humzacov.oragniseme.databinding.ActivityMicrophoneBinding binding =
                ActivityMicrophoneBinding.inflate(getLayoutInflater()); //set binding object

        //instantiate presenter
        MicrophonePresenter microphonePresenter = new MicrophonePresenter(this, binding);

        //set view listeners
        binding.startRecordingButton.setOnClickListener(microphonePresenter.startRecordListener);
        binding.finishRecordingButton.setOnClickListener(microphonePresenter.finishRecordListener);

        binding.playAudioButton.setOnClickListener(microphonePresenter.playAudioListener);
        binding.stopAudioButton.setOnClickListener(microphonePresenter.stopAudioListener);

        binding.saveRecordingButton.setOnClickListener(microphonePresenter.saveAudioListener);

        //set content view
        setContentView(binding.getRoot());
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //method called when requesting permission to record microphone
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (!permissionToRecordAccepted ) finish();
    }


}