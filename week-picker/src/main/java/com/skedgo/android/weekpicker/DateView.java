package com.skedgo.android.weekpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DateView extends RelativeLayout {

    private TextView mDayOfWeekView;
    private TextView mDayOfMonthView;
    private ImageView mTodayView;

    public DateView(Context context) {
        super(context);
        initLayout();
    }

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public DateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout();
    }

    public TextView getDayOfWeekView() {
        return mDayOfWeekView;
    }

    public TextView getDayOfMonthView() {
        return mDayOfMonthView;
    }

    public ImageView getTodayView() {
        return mTodayView;
    }

    private void initLayout() {
        // Performs merging layout.
        LayoutInflater.from(getContext()).inflate(R.layout.date_view, this, true);

        // Finds child views.
        mDayOfWeekView = (TextView) findViewById(R.id.dayOfWeekView);
        mDayOfMonthView = (TextView) findViewById(R.id.dayOfMonthView);
        mTodayView = (ImageView) findViewById(R.id.todayView);
    }
}