package com.example.appbandochoi.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

import com.example.appbandochoi.R;
import com.example.appbandochoi.constants.Constants;

import java.util.Calendar;

public class MonthYearPickerDialog extends DialogFragment {
    private static final int MIN_YEAR = 2010;
    private static final int MAX_YEAR = 2030;
    private DatePickerDialog.OnDateSetListener listener;
    private OnMonthYearSetListener getListener;
    private int selectedMonth;
    private int selectedYear;
    private Context context;

    public void setListener(DatePickerDialog.OnDateSetListener listener, OnMonthYearSetListener getListener, Context context) {
        this.listener = listener;
        this.getListener = getListener;
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        // Layout
        View dialog = inflater.inflate(R.layout.fragement_datetime_picker_dialog, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month_popup);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year_popup);

        int month = cal.get(Calendar.MONTH);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(month);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                        DatePicker datePicker = new DatePicker(getActivity());
//                        datePicker.updateDate(yearPicker.getValue(), monthPicker.getValue(), 0);
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                        // Save the selected month and year
                        selectedMonth = monthPicker.getValue();
                        selectedYear = yearPicker.getValue();

                        // Notify the listener with the selected values
                        getListener.onMonthYearSet(selectedMonth, selectedYear);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonthYearPickerDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public interface OnMonthYearSetListener {
        void onMonthYearSet(int selectedMonth, int selectedYear);
    }
}