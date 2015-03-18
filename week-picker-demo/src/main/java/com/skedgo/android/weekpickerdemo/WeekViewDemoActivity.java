package com.skedgo.android.weekpickerdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.skedgo.android.weekpicker.WeekFragment;

import java.util.Calendar;

public class WeekViewDemoActivity extends FragmentActivity {

    public static final String KEY_SELECTED_DATE = "selectedDate";

    private WeekFragment mWeekFragment;

    public static Intent newIntent(Context context, Calendar selectedDate) {
        Intent intent = new Intent(context, WeekViewDemoActivity.class);
        intent.putExtra(KEY_SELECTED_DATE, selectedDate);
        return intent;
    }

    public WeekFragment getWeekFragment() {
        return mWeekFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view_demo);

        if (savedInstanceState == null) {
            Calendar selectedDate = (Calendar) getIntent().getSerializableExtra(KEY_SELECTED_DATE);
            mWeekFragment = WeekFragment.newInstance(selectedDate);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.weekLayout, mWeekFragment)
                    .commit();
        } else {
            mWeekFragment = (WeekFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.weekLayout);
        }
    }
}