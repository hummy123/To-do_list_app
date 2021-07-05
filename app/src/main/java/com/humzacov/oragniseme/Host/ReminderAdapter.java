package com.humzacov.oragniseme.Host;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.humzacov.oragniseme.AddReminder.Reminder;
import com.humzacov.oragniseme.R;
import com.humzacov.oragniseme.databinding.ActivityHostBinding;
import com.humzacov.oragniseme.databinding.ReminderRowBinding;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderHolder> {

    private final HostActivity activity;
    private final ArrayList<Reminder> reminderList;
    private final ActivityHostBinding binding;

    public ReminderAdapter(HostActivity activity, ArrayList<Reminder> reminderList, ActivityHostBinding binding) {
        this.activity = activity;
        this.reminderList = reminderList;
        this.binding = binding;
    }

    @NonNull
    @NotNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ReminderRowBinding binding = ReminderRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReminderHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReminderAdapter.ReminderHolder holder, int position) {
        holder.binding.titleText.setText(this.reminderList.get(position).getTitle());

        //convert time from utc to user's current time
        String[] reminderTime = convertTime(this.reminderList.get(position).getTime());
        holder.binding.dateText.setText(reminderTime[0]);
        holder.binding.timeText.setText(reminderTime[1]);

        //download photo/speech files and show on ui
        downloadPhoto(this.reminderList.get(position).getPhoto(), holder.binding);

        //show audio button if reminder has audio attached
        audioUI(this.reminderList.get(position).getRecordings(), holder.binding);
    }

    private void audioUI(String link, ReminderRowBinding binding) {
        if (link != null) {
            //different background colour for reminders with audio to play
            binding.playButton.setVisibility(View.VISIBLE);
            playAudio(link, binding); //attach listener to play audio on click
        }
    }

    private void playAudio(String link, ReminderRowBinding binding) {
        //listener that plays relevant audio if button clicked
        // code snippet adapted from https://developer.android.com/guide/topics/media/mediaplayer#java

        binding.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                try {
                    mediaPlayer.setDataSource(link);
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        });
    }

    private String[] convertTime(String utcTime) {
        /* method converts from UTC (how time is stored
         * on the database to user's local time zone.
         * Returns separate date and time strings. */
        if (!utcTime.equals("")) {
            ZonedDateTime systemTime = ZonedDateTime.parse(utcTime); //parse string as object
            TimeZone curZone = TimeZone.getDefault(); //get user's current time zone
            ZonedDateTime userTime = systemTime.withZoneSameInstant(curZone.toZoneId()); //convert time zone

            String userStrTime = userTime.toString(); //convert to string
            String date = userStrTime.substring(0, 10); //get date portion of string
            String time = userStrTime.substring(11,16); //get time portion of string
            return new String[]{date, time};
        } else {
            return new String[]{"", ""}; //return empty strings if no time for reminder
        }
    }

    private void downloadPhoto(String link, ReminderRowBinding binding) {
        /* display image if link is not null; if null, display default image. */
        if (link != null) {
            //if reminder has photo link, download file and set to image view
            Glide.with(binding.getRoot()).load(link).into(binding.reminderImage);
        }

        //set size of imageview (including default image)
        binding.reminderImage.getLayoutParams().height = 200;
        binding.reminderImage.getLayoutParams().width = 200;
        binding.reminderImage.requestLayout();
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class ReminderHolder extends RecyclerView.ViewHolder {

        private final ReminderRowBinding binding;

        public ReminderHolder(@NonNull ReminderRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
