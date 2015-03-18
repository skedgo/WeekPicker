package com.skedgo.android.weekpickerdemo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.skedgo.android.weekpicker.IWeekPickerController;
import com.skedgo.android.weekpicker.WeekPickerFragment;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

public class WeekPickerDemoActivityTest extends ActivityInstrumentationTestCase2<WeekPickerDemoActivity> {

    private static final int MIDDLE_WEEK_POSITION = WeekPickerFragment.MAX_WEEK_COUNT / 2;

    private IWeekPickerController mWeekPickerController;
    private ViewPager mWeekPickerView;

    private Calendar mInitialSelectedDate;
    private WeekPickerDemoActivity mActivity;

    public WeekPickerDemoActivityTest() {
        super(WeekPickerDemoActivity.class);
    }

    public void testShouldStandInCorrectInitialWeekPosition() {
        assertThat(mWeekPickerView.getCurrentItem())
                .isEqualTo(MIDDLE_WEEK_POSITION);
    }

    public void testShouldHaveCorrectInitialSelectedDate() {
        assertThat(mWeekPickerController.getSelectedDate())
                .isEqualTo(mInitialSelectedDate.getTime());
    }

    @UiThreadTest
    public void testShouldNavigateToNextWeekProperly() {
        // Navigate the picker to the next week.
        int expectedWeekPosition = MIDDLE_WEEK_POSITION + 1;
        mWeekPickerView.setCurrentItem(expectedWeekPosition);

        Calendar expectedDate = (Calendar) mInitialSelectedDate.clone();
        expectedDate.set(Calendar.DAY_OF_MONTH, 13);

        assertThat(mWeekPickerController.getSelectedDate())
                .isEqualTo(expectedDate.getTime());
    }

    @UiThreadTest
    public void testShouldNavigateToPreviousWeekProperly() {
        // Navigate the picker to the previous week.
        int expectedWeekPosition = MIDDLE_WEEK_POSITION - 1;
        mWeekPickerView.setCurrentItem(expectedWeekPosition);

        Calendar expectedDate = (Calendar) mInitialSelectedDate.clone();
        expectedDate.set(Calendar.MONTH, Calendar.JULY);
        expectedDate.set(Calendar.DAY_OF_MONTH, 30);

        assertThat(mWeekPickerController.getSelectedDate())
                .isEqualTo(expectedDate.getTime());
    }

    @UiThreadTest
    public void testShouldNavigateToNextNextWeekProperly() {
        // Navigate the picker to the next next week.
        int expectedWeekPosition = MIDDLE_WEEK_POSITION + 2;
        mWeekPickerView.setCurrentItem(expectedWeekPosition);

        Calendar expectedDate = (Calendar) mInitialSelectedDate.clone();
        expectedDate.set(Calendar.DAY_OF_MONTH, 20);

        assertThat(mWeekPickerController.getSelectedDate())
                .isEqualTo(expectedDate.getTime());
    }

    @UiThreadTest
    public void testShouldNavigateToPreviousPreviousWeekProperly() {
        // Navigate the picker to the previous week.
        int expectedWeekPosition = MIDDLE_WEEK_POSITION - 2;
        mWeekPickerView.setCurrentItem(expectedWeekPosition);

        Calendar expectedDate = (Calendar) mInitialSelectedDate.clone();
        expectedDate.set(Calendar.MONTH, Calendar.JULY);
        expectedDate.set(Calendar.DAY_OF_MONTH, 23);

        assertThat(mWeekPickerController.getSelectedDate())
                .isEqualTo(expectedDate.getTime());
    }

    public void testShouldRestoreWeekPositionProperly() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                // Navigate the picker to the next next week.
                testShouldNavigateToNextNextWeekProperly();
            }
        });

        recreateActivity();

        int expectedWeekPosition = MIDDLE_WEEK_POSITION + 2;
        assertThat(mWeekPickerView.getCurrentItem())
                .isEqualTo(expectedWeekPosition);
    }

    public void testShouldRestoreSelectedDateProperly() {
        getInstrumentation().runOnMainSync(new Runnable() {

            @Override
            public void run() {
                // Navigate the picker to the next next week.
                testShouldNavigateToNextNextWeekProperly();
            }
        });

        recreateActivity();

        Calendar expectedDate = (Calendar) mInitialSelectedDate.clone();
        expectedDate.set(Calendar.DAY_OF_MONTH, 20);

        assertThat(mWeekPickerController.getSelectedDate())
                .isEqualTo(expectedDate.getTime());
    }

    public void testWeekPickerDemo() throws InterruptedException {
        new CountDownLatch(0).await();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mInitialSelectedDate = Calendar.getInstance();
        mInitialSelectedDate.setFirstDayOfWeek(Calendar.MONDAY);
        mInitialSelectedDate.set(Calendar.MONTH, Calendar.AUGUST);
        mInitialSelectedDate.set(Calendar.DAY_OF_MONTH, 6);
        mInitialSelectedDate.set(Calendar.YEAR, 2014);

        Intent mockIntent = WeekPickerDemoActivity.newIntent(
                getInstrumentation().getTargetContext(),
                mInitialSelectedDate.getTimeInMillis());
        setActivityIntent(mockIntent);

        launchWeekPickerDemo();
    }

    private void launchWeekPickerDemo() {
        mActivity = getActivity();
        mWeekPickerController = mActivity.getWeekPickerFragment();
        mWeekPickerView = mActivity.getWeekPickerFragment().getWeekPickerView();
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

        launchWeekPickerDemo();
    }
}