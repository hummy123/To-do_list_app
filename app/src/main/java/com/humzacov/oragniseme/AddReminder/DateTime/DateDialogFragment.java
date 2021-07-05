package com.humzacov.oragniseme.AddReminder.DateTime;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import com.humzacov.oragniseme.AddReminder.AddReminderPresenter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Month;

public class DateDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final AddReminderPresenter presenter;

    public DateDialogFragment(AddReminderPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public @NotNull DatePickerDialog onCreateDialog(Bundle savedInstanceState) {
        //display current date by default
        LocalDate now = LocalDate.now(); //get current date
        
        // return new DatePickerDialog showing today's date
        return new DatePickerDialog(getActivity(), this, now.getYear(), now.getMonthValue(), now.getDayOfMonth());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        presenter.setDate(year, month, dayOfMonth);
    }
}
