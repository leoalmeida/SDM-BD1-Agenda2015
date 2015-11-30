package br.edu.ifspsaocarlos.sdm.agenda2015.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by LeonardoAlmeida on 29/11/15.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    DateListerner listerner;

    public interface DateListerner{
        public void onReturnDate(String date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        listerner = (DateListerner) getActivity();

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        Calendar newDate = Calendar.getInstance();
        newDate.set(year, month, day);
        if (listerner!= null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            listerner.onReturnDate(sdf.format(newDate.getTime()));
        }
    }
}
