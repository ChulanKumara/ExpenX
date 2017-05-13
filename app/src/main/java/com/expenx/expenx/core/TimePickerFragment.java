package com.expenx.expenx.core;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import com.expenx.expenx.activity.ReminderActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
                            implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), android.R.style.Theme_DeviceDefault_Dialog, this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Toast.makeText(getActivity().getApplicationContext(),"Reminder Time is "+String.format("%02d",hourOfDay)+":"+String.format("%02d",minute),Toast.LENGTH_LONG).show();

        ReminderActivity.calendatTime.set(2017,1,1,hourOfDay,minute);
    }
}