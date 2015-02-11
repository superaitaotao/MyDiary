package com.here.superaitaotaotv.mydiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import com.example.superaitaotaotv.mydiary.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by superaitaotaoTV on 22/7/14.
 */
public class DatePickerFragment extends DialogFragment {

    private View theView;

    public static final String Extra_Date = "com.example.superaitaotaotv.inotebook.aDate";
    private DatePicker datePicker;

    public static DatePickerFragment newInstance(Date aDate){

        Bundle args = new Bundle();
        args.putSerializable(Extra_Date, aDate);
        DatePickerFragment newDatePickerFragment = new DatePickerFragment();
        newDatePickerFragment.setArguments(args);
        return  newDatePickerFragment;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        theView = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_date_picker, null);

        datePicker = (DatePicker) theView.findViewById(R.id.date_picker);

        Date newDate = (Date) getArguments().getSerializable(Extra_Date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(newDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePicker.init(year,month,day,null);

        return new AlertDialog.Builder(getActivity())
                .setView(theView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Calendar newCal = new GregorianCalendar(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                        Date newDate = newCal.getTime();
                        Intent returnNewDateIntent = new Intent();
                        returnNewDateIntent.putExtra(Extra_Date,newDate);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, returnNewDateIntent);
                    }
                })
                .setTitle(getActivity().getString(R.string.date_picker_title))
                .create();


    }
}
