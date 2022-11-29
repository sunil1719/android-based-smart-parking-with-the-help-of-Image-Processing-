package com.example.parksure;

//public interface DatePickerFragment1 {
//}


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.DialogFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.lang.reflect.Field;
import java.util.Calendar;
//import java.util.Date;


public class DatePickerFragment1 extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /*Calendar calendar = Calendar.getInstance();
        int year    = calendar.get(Calendar.YEAR);
        int month   = calendar.get(Calendar.MONTH);
        int day     = calendar.get(Calendar.DAY_OF_MONTH);*/
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //DatePickerDialog dialog = new DatePickerDialog(getContext(), listener, year, month, day); insted of this....used belod edits
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        Field mDatePickerField;
        try {
            mDatePickerField = dialog.getClass().getDeclaredField("mDatePicker");
            mDatePickerField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000*3600*24*10);
        return dialog;
    }
    /* not usefull anymore
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }*/
}