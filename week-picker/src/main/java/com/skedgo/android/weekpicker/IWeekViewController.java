package com.skedgo.android.weekpicker;

import java.util.Date;

/**
 * Looks identical to {@link com.skedgo.android.weekpicker.IWeekPickerController}, right?
 */
public interface IWeekViewController {

    Date getSelectedDate();

    void setSelectedDate(Date selectedDate);

    void setOnDateSelectedListener(OnDateSelectedListener listener);

    WeekView getWeekView();
}