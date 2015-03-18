package com.skedgo.android.weekpickerdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.skedgo.android.weekpicker.DateView;
import com.skedgo.android.weekpicker.OnDateSelectedListener;
import com.skedgo.android.weekpicker.WeekFragment;
import com.skedgo.android.weekpicker.WeekView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class WeekViewDemoActivityTest extends ActivityInstrumentationTestCase2<WeekViewDemoActivity> {

    private WeekViewDemoActivity mActivity;
    private WeekFragment mWeekFragment;
    private WeekView mWeekView;

    private Calendar mInitialSelectedDate;
    private ArrayList<Calendar> mExpectedDateRange;

    public WeekViewDemoActivityTest() {
        super(WeekViewDemoActivity.class);
    }

    public void testWeekViewDemo() throws InterruptedException {
        new CountDownLatch(0).await();
    }

    public void testShouldShowCorrectDaysOfMonth() {
        List<DateView> dateViewList = Arrays.asList(mWeekView.getDateViews());
        for (int i = 0; i < dateViewList.size(); i++) {
            CharSequence dayOfMonthText = dateViewList.get(i).getDayOfMonthView().getText();
            String expectedDayOfMonth = Integer.toString(mExpectedDateRange.get(i).get(Calendar.DAY_OF_MONTH));
            assertThat(dayOfMonthText)
                    .isEqualTo(expectedDayOfMonth);
        }
    }

    public void testShouldShowCorrectDaysOfWeek() {
        String[] expectedDaysOfWeek = new String[] {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        List<DateView> dateViewList = Arrays.asList(mWeekView.getDateViews());
        for (int i = 0; i < dateViewList.size(); i++) {
            CharSequence dayOfWeekText = dateViewList.get(i).getDayOfWeekView().getText();
            assertThat(dayOfWeekText)
                    .isEqualTo(expectedDaysOfWeek[i]);
        }
    }

    public void testShouldRestoreWeekProperly() {
        recreateActivity();

        testShouldShowCorrectDaysOfMonth();
        testShouldShowCorrectDaysOfWeek();
    }

    public void testShouldRestoreSelectedDateProperly() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mWeekFragment.getWeekView().getDateViews()[5].performClick();
            }
        });

        recreateActivity();

        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(5).getTime());
    }

    @UiThreadTest
    public void testShouldEmitCorrectSelectedDateToSubscribers() {
        final AtomicReference<Date> selectedDateTracker = new AtomicReference<Date>();
        mWeekFragment.setOnDateSelectedListener(new OnDateSelectedListener() {

            @Override
            public void onDateSelected(Date date) {
                selectedDateTracker.set(date);
            }
        });

        mWeekFragment.getWeekView().getDateViews()[0].performClick();
        assertThat(selectedDateTracker.get())
                .isEqualTo(mExpectedDateRange.get(0).getTime());

        mWeekFragment.getWeekView().getDateViews()[1].performClick();
        assertThat(selectedDateTracker.get())
                .isEqualTo(mExpectedDateRange.get(1).getTime());

        mWeekFragment.getWeekView().getDateViews()[2].performClick();
        assertThat(selectedDateTracker.get())
                .isEqualTo(mExpectedDateRange.get(2).getTime());

        mWeekFragment.getWeekView().getDateViews()[3].performClick();
        assertThat(selectedDateTracker.get())
                .isEqualTo(mExpectedDateRange.get(3).getTime());

        mWeekFragment.getWeekView().getDateViews()[4].performClick();
        assertThat(selectedDateTracker.get())
                .isEqualTo(mExpectedDateRange.get(4).getTime());

        mWeekFragment.getWeekView().getDateViews()[5].performClick();
        assertThat(selectedDateTracker.get())
                .isEqualTo(mExpectedDateRange.get(5).getTime());

        mWeekFragment.getWeekView().getDateViews()[6].performClick();
        assertThat(selectedDateTracker.get())
                .isEqualTo(mExpectedDateRange.get(6).getTime());
    }

    @UiThreadTest
    public void testShouldUpdateCorrectSelectedDate() {
        mWeekFragment.getWeekView().getDateViews()[0].performClick();
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(0).getTime());

        mWeekFragment.getWeekView().getDateViews()[1].performClick();
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(1).getTime());

        mWeekFragment.getWeekView().getDateViews()[2].performClick();
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(2).getTime());

        mWeekFragment.getWeekView().getDateViews()[3].performClick();
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(3).getTime());

        mWeekFragment.getWeekView().getDateViews()[4].performClick();
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(4).getTime());

        mWeekFragment.getWeekView().getDateViews()[5].performClick();
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(5).getTime());

        mWeekFragment.getWeekView().getDateViews()[6].performClick();
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(6).getTime());
    }

    @UiThreadTest
    public void testSetSelectedDateSetterShouldUpdateWeekViewSelectedItemProperly() {
        mWeekFragment.setSelectedDate(mExpectedDateRange.get(0).getTime());
        assertThat(mWeekFragment.getWeekView().getSelectedItem()).isEqualTo(0);

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(1).getTime());
        assertThat(mWeekFragment.getWeekView().getSelectedItem()).isEqualTo(1);

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(2).getTime());
        assertThat(mWeekFragment.getWeekView().getSelectedItem()).isEqualTo(2);

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(3).getTime());
        assertThat(mWeekFragment.getWeekView().getSelectedItem()).isEqualTo(3);

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(4).getTime());
        assertThat(mWeekFragment.getWeekView().getSelectedItem()).isEqualTo(4);

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(5).getTime());
        assertThat(mWeekFragment.getWeekView().getSelectedItem()).isEqualTo(5);

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(6).getTime());
        assertThat(mWeekFragment.getWeekView().getSelectedItem()).isEqualTo(6);
    }

    @UiThreadTest
    public void testShouldUpdateCorrectSelectedDate2() {
        mWeekFragment.setSelectedDate(mExpectedDateRange.get(0).getTime());
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(0).getTime());

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(1).getTime());
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(1).getTime());

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(2).getTime());
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(2).getTime());

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(3).getTime());
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(3).getTime());

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(4).getTime());
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(4).getTime());

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(5).getTime());
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(5).getTime());

        mWeekFragment.setSelectedDate(mExpectedDateRange.get(6).getTime());
        assertThat(mWeekFragment.getSelectedDate())
                .isEqualTo(mExpectedDateRange.get(6).getTime());
    }

    @UiThreadTest
    public void testShouldThrowExceptionIfOutOfDateRange() {
        Calendar dateBefore = (Calendar) mInitialSelectedDate.clone();
        dateBefore.set(Calendar.DAY_OF_MONTH, 3);

        try {
            mWeekFragment.setSelectedDate(dateBefore.getTime());
            fail("Should throw exception due to setting a date that is before " + mExpectedDateRange.get(0).getTime());
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        Calendar dateAfter = (Calendar) mInitialSelectedDate.clone();
        dateAfter.set(Calendar.DAY_OF_MONTH, 11);

        try {
            mWeekFragment.setSelectedDate(dateBefore.getTime());
            fail("Should throw exception due to setting a date that is after " + mExpectedDateRange.get(6).getTime());
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.MONTH, Calendar.AUGUST);
        calendar.set(Calendar.YEAR, 2014);

        // Initialize a date range between August 4 to August 10.
        mExpectedDateRange = new ArrayList<Calendar>();
        for (int dayOfMonth = 4; dayOfMonth <= 10; dayOfMonth++) {
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mExpectedDateRange.add((Calendar) calendar.clone());
        }

        // Our selected date is August 6.
        calendar.set(Calendar.DAY_OF_MONTH, 6);
        mInitialSelectedDate = calendar;

        Context targetContext = getInstrumentation().getTargetContext();
        Intent mockIntent = WeekViewDemoActivity.newIntent(targetContext, mInitialSelectedDate);
        setActivityIntent(mockIntent);

        launchWeekViewDemo();
    }

    private void launchWeekViewDemo() {
        mActivity = getActivity();
        mWeekFragment = mActivity.getWeekFragment();
        mWeekView = mWeekFragment.getWeekView();
    }

    /**
     * FIXME: Need to check if this works in all cases
     * <p/>
     * See more at: http://stackoverflow.com/a/21112638/2563009
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void recreateActivity() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                mActivity.recreate();
            }
        });
        setActivity(null);

        launchWeekViewDemo();
    }
}