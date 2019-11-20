package com.example.emiandroid.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.emiandroid.R;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NumberPickerFragment extends DialogFragment {
    private com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener valueChangeListener;

    @SuppressLint("WrongConstant")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final com.shawnlin.numberpicker.NumberPicker numberPicker = new com.shawnlin.numberpicker.NumberPicker(getActivity());

        numberPicker.setDividerColorResource(R.color.colorPrimary);
        numberPicker.setSelectedTextColorResource(R.color.colorPrimary);
        numberPicker.setSelectedTextSize(R.dimen.selected_text_size);
        numberPicker.setTextColorResource(R.color.colorAccent);
        numberPicker.setTextSize(R.dimen.text_size);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(90);
        numberPicker.setOrientation(LinearLayout.HORIZONTAL);

        numberPicker.setFadingEdgeEnabled(true);
        numberPicker.setScrollerEnabled(true);
        numberPicker.setWrapSelectorWheel(true);

        numberPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Click on current value");
            }
        });

        numberPicker.setOnValueChangedListener(new com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(com.shawnlin.numberpicker.NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, String.format(Locale.US, "oldVal: %d, newVal: %d", oldVal, newVal));

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Weightage");
        builder.setMessage("Select weightage for the selected instalment");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker, numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker, numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

    public com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}
