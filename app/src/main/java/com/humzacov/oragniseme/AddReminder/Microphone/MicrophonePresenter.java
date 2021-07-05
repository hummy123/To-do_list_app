package com.humzacov.oragniseme.AddReminder.Microphone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.humzacov.oragniseme.databinding.ActivityMicrophoneBinding;

import java.io.File;
import java.io.IOException;

   public class MicrophonePresenter {
    private final Activity microphoneActivity;
    private final ActivityMicrophoneBinding binding;

    //objects used for storing recording-data/play-back data
    private MediaRecorder recorder;
    private MediaPlayer player;

    //file path and file name to save recording in
    private final String FILE_PATH;
    private final String FILE_NAME = "TEMP_RECORDING.3gp";

    //tags for debugging via log
    private final String RECORDING_TAG = "RECORDING";
    private final String NEWFILE_TAG = "CREATE FILE";

    public MicrophonePresenter(Activity microphoneActivity, ActivityMicrophoneBinding binding) {
        this.microphoneActivity = microphoneActivity;
        this.binding = binding;

        //store file path where recordings will be saved
        this.FILE_PATH = microphoneActivity.getApplicationContext().getFilesDir() + "/Speech Recordings/";

        //create file and directory to store recordings in if they don't exist
        setUpDirectory();
    }

    private void setUpDirectory() {
        //create directory where recording is stored
        File recordDir = new File(FILE_PATH);
        recordDir.mkdirs();

        //create recording file
        File test = new File(FILE_PATH + FILE_NAME);
        try {
            Log.i(NEWFILE_TAG, "File created: " + test.createNewFile());
        } catch(Exception e) {
            Log.e(NEWFILE_TAG, e.toString());
        }
    }

    private void startButton() {
        //request permission to record microphone
        ActivityCompat.requestPermissions(microphoneActivity, new String[] { Manifest.permission.RECORD_AUDIO },
                10);

        //make buttons appear as they did when starting activity
        startView();

        //call method to start audio-recording object
        startRecording();
    }

    private void startView() {
        /* hide button to start recording
         * and show button to finish recording */
        binding.startRecordingButton.setVisibility(View.GONE);
        binding.finishRecordingButton.setVisibility(View.VISIBLE);

        /* show play button and hide stop button
         * because it does not make sense to show stop button
         * when no audio has been recorded or previous audio discarded. */
        binding.playAudioButton.setVisibility(View.VISIBLE);
        binding.stopAudioButton.setVisibility(View.GONE);

        //disable other buttons to block input until recording is finished
        binding.playAudioButton.setEnabled(false);
        binding.saveRecordingButton.setEnabled(false);
    }

    private File pointToFile() {
        /* create pointer to file where recording is stored
         * or where audio is played back from. */
        return new File(FILE_PATH + FILE_NAME);
    }

    private void startRecording() {
        //create temporary file to store audio in
        File speechFile = pointToFile();

        //configure options (recording device, file type, etc.)
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(speechFile.getAbsolutePath());

        //prepare and start recording
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(RECORDING_TAG, e.toString());
        }
        recorder.start();

        /* store recording object as class field, so
         * it can be accessed to finish the recording.*/
        this.recorder = recorder;
    }

    private void finishButton() {
        finishView(); //call method to adjust UI as desired when recording finished
        finishRecording(); //call method to end recording
    }

    private void finishView() {
        /* hide button to finish recording
         * and show button to start recording
         * in case user is dissatisfied and wants to record again. */
        binding.finishRecordingButton.setVisibility(View.GONE);
        binding.startRecordingButton.setVisibility(View.VISIBLE);

        //enable play-back and save-audio buttons
        binding.playAudioButton.setEnabled(true);
        binding.saveRecordingButton.setEnabled(true);
    }

    private void finishRecording() {
        this.recorder.stop(); //stop recording
        this.recorder.release(); //free up memory resources taken by file
    }

    private void playAudioButton() {
        playView(); //call method to config UI as desired
        playAudio(); //call method to play audio
    }

    private void playView() {
        /* hide button to play audio,
         * and show button to stop audio */
        binding.playAudioButton.setVisibility(View.GONE);
        binding.stopAudioButton.setVisibility(View.VISIBLE);
    }

    private void playAudio() {
        /* method to play audio */
        preparePlay();
        this.player.start(); //start playback
    }

    private void preparePlay() {
        /* method to set up player object for playback */
        //create pointer to file where recording is stored
        File speechFile = pointToFile();

        //create media player object so audio can be played back
        this.player = MediaPlayer.create(
                microphoneActivity.getApplicationContext(),
                Uri.fromFile(speechFile));

        /* anonymous function below changes UI when
         * audio has finished playing. */
        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopView();
            }
        });
    }

    private void stopAudioButton() {
        stopView(); //set desire UI config when stop is pressed
        stopAudio(); //call method to stop audio
    }

    private void stopView() {
        /* hide button to stop audio,
         * and show button to play audio */
        binding.playAudioButton.setVisibility(View.VISIBLE);
        binding.stopAudioButton.setVisibility(View.GONE);
    }

    private void stopAudio() {
        this.player.stop(); //stop audio
        this.player.release(); //free memory resources
    }

    private void saveRecording() {
        /* method to save recording and finish activity */
        Intent audioIntent = new Intent();

        //return recording to calling activity
        audioIntent.putExtra("audio", pointToFile());
        microphoneActivity.setResult(Activity.RESULT_OK, audioIntent);
        microphoneActivity.finish(); //finish activity
    }

    protected View.OnClickListener startRecordListener = v -> startButton();
    protected View.OnClickListener finishRecordListener = v -> finishButton();

    protected View.OnClickListener playAudioListener = v -> playAudioButton();
    protected View.OnClickListener stopAudioListener = v -> stopAudioButton();

    protected View.OnClickListener saveAudioListener = v -> saveRecording();
}
