package com.riteshjaiswal.authenticationapp;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.Format;
import java.util.Locale;



public class IndianCurrencyFormatter implements IAxisValueFormatter {
    Format format;

    public IndianCurrencyFormatter() {
        format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en","in"));
    }


    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return format.format(value);
    }
}
