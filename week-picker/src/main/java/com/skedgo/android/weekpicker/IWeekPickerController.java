package com.skedgo.android.weekpicker;

import android.support.v4.view.ViewPager;
import android.text.format.Time;

import java.util.Date;

public interface IWeekPickerController {
    Date getSelectedDate();
    void setSelectedDate(Time date);
    void setOnDateSelectedListener(OnDateSelectedListener listener);
    ViewPager getWeekPickerView();
}