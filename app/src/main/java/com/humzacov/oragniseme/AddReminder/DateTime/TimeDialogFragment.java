package com.humzacov.oragniseme.AddReminder.DateTime;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.humzacov.oragniseme.AddReminder.AddReminderPresenter;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public class TimeDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private final AddReminderPresenter presenter;
    public TimeDialogFragment(AddReminderPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {
        //display current time by default
        LocalDateTime now = LocalDateTime.now(); //get time in user's time zone

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, now.getHour(), now.getMinute(),
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        presenter.setTime(hourOfDay, minute); //call funcion in presenter to update time
    }
}
