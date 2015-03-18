package com.skedgo.android.weekpickerdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.skedgo.android.weekpicker.OnDateSelectedListener;
import com.skedgo.android.weekpicker.WeekPickerFragment;

import java.util.Calendar;
import java.util.Date;

public class WeekPickerDemoActivity extends FragmentActivity {

    public static final String KEY_SELECTED_DATE_IN_MILLIS = "selectedDateInMillis";

    private WeekPickerFragment mWeekPickerFragment;

    public static Intent newIntent(Context context, long selectedDateInMillis) {
        Intent intent = new Intent(context, WeekPickerDemoActivity.class);
        intent.putExtra(KEY_SELECTED_DATE_IN_MILLIS, selectedDateInMillis);
        return intent;
    }

    public WeekPickerFragment getWeekPickerFragment() {
        return mWeekPickerFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_week_picker_demo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.todayMenuItem:
                goToToday();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_picker_demo);

        if (savedInstanceState == null) {
            long selectedDateInMillis = getIntent().getLongExtra(KEY_SELECTED_DATE_IN_MILLIS, System.currentTimeMillis());
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selectedDateInMillis);

            mWeekPickerFragment = WeekPickerFragment.newInstance(selectedDate);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.weekPickerLayout, mWeekPickerFragment)
                    .commit();
        } else {
            mWeekPickerFragment = (WeekPickerFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.weekPickerLayout);
        }

        mWeekPickerFragment.setOnDateSelectedListener(new OnDateSelectedListener() {

            @Override
            public void onDateSelected(Date date) {
                ((TextView) findViewById(R.id.timeTextView)).setText(date.toString());
            }
        });
    }

    private void goToToday() {
        Time now = new Time();
        now.setToNow();

        mWeekPickerFragment.setSelectedDate(now);
    }
}