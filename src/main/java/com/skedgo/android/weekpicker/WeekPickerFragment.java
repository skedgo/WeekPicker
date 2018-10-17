package com.skedgo.android.weekpicker;

import android.os.Build;
import android.os.Bundle;
import androidx.core.app.Fragment;
import androidx.core.app.FragmentManager;
import androidx.core.app.FragmentStatePagerAdapter;
import androidx.core.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class WeekPickerFragment extends Fragment {
  public static final String KEY_SELECTED_DATE = "selectedDate";
  public static final String KEY_WEEK_POSITION = "weekPosition";
  public static final String KEY_PIVOT_DATE = "pivotDate";
  public static final int MAX_WEEK_COUNT = 200;
  private static final String TAG = "AwesomePicker";
  private SwipeDisableViewPager mWeekPickerView;
  private Calendar mSelectedDate;
  private int mWeekPosition;
  private int mWeekStart;
  // TODO: Why call 'PivotDate'?
  private Calendar mPivotDate;
  private OnDateSelectedListener mOnDateSelectedListener;
  private OnDateSelectedListener mOnWeekViewDateSelectedListener = new OnDateSelectedListener() {

    @Override
    public void onDateSelected(Date date) {
      mSelectedDate.setTime(date);
      emitSelectedDate();
    }
  };

  public static WeekPickerFragment newInstance(Calendar selectedDate, int weekStart) {
    Bundle args = new Bundle();
    args.putSerializable(KEY_SELECTED_DATE, selectedDate);
    args.putSerializable(KEY_PIVOT_DATE, (Calendar) selectedDate.clone());

    WeekPickerFragment fragment = new WeekPickerFragment();
    fragment.setArguments(args);
    fragment.mSelectedDate = selectedDate;
    fragment.mWeekStart = weekStart;
    return fragment;
  }

  public static WeekPickerFragment newInstance(Calendar selectedDate) {
    Bundle args = new Bundle();
    args.putSerializable(KEY_SELECTED_DATE, selectedDate);
    args.putSerializable(KEY_PIVOT_DATE, (Calendar) selectedDate.clone());

    WeekPickerFragment fragment = new WeekPickerFragment();
    fragment.setArguments(args);
    fragment.mSelectedDate = selectedDate;
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
      mSelectedDate = (Calendar) getArguments().getSerializable(KEY_SELECTED_DATE);
      mWeekPosition = savedInstanceState.getInt(KEY_WEEK_POSITION);
    } else {
      mWeekPosition = MAX_WEEK_COUNT / 2;
    }

    mPivotDate = (Calendar) getArguments().get(KEY_PIVOT_DATE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mWeekPickerView = (SwipeDisableViewPager) inflater.inflate(R.layout.fragment_week_picker, container, false);
    return mWeekPickerView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    final WeekFragmentPagerAdapter weekPagerAdapter = new WeekFragmentPagerAdapter(getFragmentManager(), mPivotDate);
    mWeekPickerView.setAdapter(weekPagerAdapter);

    if (savedInstanceState == null) {
      mWeekPickerView.setCurrentItem(mWeekPosition, false);
    }

    mWeekPickerView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

      @Override
      public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected: " + position);

        int weekOffset = position - mWeekPosition;
        mWeekPosition = position;

        // Should change the date accordingly.
        mSelectedDate.add(Calendar.WEEK_OF_YEAR, weekOffset);

        // The following fragment can be null if this listener gets called
        // after recreating the Activity.
        WeekFragment selectedWeekFragment = weekPagerAdapter.fragmentArray.get(position);
        if (selectedWeekFragment != null) {
          selectedWeekFragment.setSelectedDate(mSelectedDate.getTime());
        }
      }
    });
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(KEY_WEEK_POSITION, mWeekPosition);
  }

  public Date getSelectedDate() {
    return mSelectedDate.getTime();
  }

  public void setSelectedDate(Time date) {
    //create a selected date Time object
    Time selectedDate = new Time();
    selectedDate.set(mSelectedDate.getTimeInMillis());
    selectedDate.normalize(false);

    //align the time so we can avoid division error in computing weekDiff (round to 0 if result is 0.9)
    selectedDate.hour = date.hour;
    selectedDate.minute = date.minute;
    selectedDate.second = date.second;
    selectedDate.normalize(false);
    //shift the weekday to match the input
    int weekDayDiff = date.weekDay - selectedDate.weekDay;
    long dayDiffSes = weekDayDiff * InSeconds.DAY;
    //then calculate the week difference
    long shiftedOldSecs = selectedDate.toMillis(false) / 1000 + dayDiffSes;
    long newSecs = date.toMillis(false) / 1000;
    //change the secs to days before comparing
    int dayDiff = (int) (Math.ceil(newSecs / InSeconds.DAY) - Math.ceil(shiftedOldSecs / InSeconds.DAY)); //can be negative
    int weekDiff = dayDiff / 7;// a week has 7 days, round to 0 if difference is less than a week

    //align current selected date to the week day
    mSelectedDate.setTimeInMillis(shiftedOldSecs * 1000);
    final WeekFragmentPagerAdapter weekPagerAdapter = (WeekFragmentPagerAdapter) mWeekPickerView.getAdapter();
    if (weekDiff == 0) {
      WeekFragment weekFragment = weekPagerAdapter.fragmentArray.get(mWeekPosition);
      weekFragment.setSelectedDate(mSelectedDate.getTime());
    } else {
      int newPosition = mWeekPosition + weekDiff;
      mWeekPickerView.setCurrentItem(newPosition);
    }
  }

  public void setOnDateSelectedListener(OnDateSelectedListener listener) {
    mOnDateSelectedListener = listener;
    emitSelectedDate();
  }

  /**
   * @param enabled If false, data selection is disabled and it's unable to swipe between weeks.
   */
  public void setEnabled(boolean enabled) {
    if (mWeekPickerView != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        mWeekPickerView.setAlpha(enabled ? 1f : 0.5f);
      }

      mWeekPickerView.setEnabled(enabled);
      final WeekFragmentPagerAdapter weekPagerAdapter = (WeekFragmentPagerAdapter) mWeekPickerView.getAdapter();
      weekPagerAdapter.enableFragments(mWeekPickerView.isEnabled());
    } // Else, FIXME: View isn't ready yet. Show enabled status later on.
  }

  /**
   * Emit latest selected date to subscribers.
   */
  protected void emitSelectedDate() {
    if (mOnDateSelectedListener != null) {
      mOnDateSelectedListener.onDateSelected(getSelectedDate());
    }
  }

  /**
   * FIXME: Use {@link TimeUnit} instead.
   */
  @Deprecated
  public static final class InSeconds {
    public static final int MINUTE = 60;
    public static final int HOUR = MINUTE * 60;
    public static final int DAY = HOUR * 24;
    public static final int WEEK = DAY * 7;
    public static final int MONTH = WEEK * 4;
    public static final int YEAR = DAY * 365; // 31,536,000 < 2^32 (4.2 billion), an int is enough
  }

  /**
   * Credit to https://gist.github.com/nesquena/c715c9b22fb873b1d259
   */
  class WeekFragmentPagerAdapter extends FragmentStatePagerAdapter {
    public final SparseArray<WeekFragment> fragmentArray = new SparseArray<WeekFragment>();

    private Calendar mPivotDate;

    public WeekFragmentPagerAdapter(FragmentManager fragmentManager, Calendar pivotDate) {
      super(fragmentManager);
      mPivotDate = (Calendar) pivotDate.clone();
    }

    @Override
    public Fragment getItem(int position) {
      int weekOffset = position - MAX_WEEK_COUNT / 2;

      // WeekFragment needs to know this date so that
      // it is able to show the week for that date.
      Calendar date = (Calendar) mPivotDate.clone();
      date.add(Calendar.WEEK_OF_YEAR, weekOffset);

      return mWeekStart != 0
          ? WeekFragment.newInstance(date, mWeekStart)
          : WeekFragment.newInstance(date);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      WeekFragment weekFragment = (WeekFragment) super.instantiateItem(container, position);
      weekFragment.setOnDateSelectedListener(mOnWeekViewDateSelectedListener);
      fragmentArray.put(position, weekFragment);
      weekFragment.setEnabled(mWeekPickerView.isEnabled());
      return weekFragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      fragmentArray.remove(position);
      super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
      return MAX_WEEK_COUNT;
    }

    public void enableFragments(boolean enable) {
      if (fragmentArray != null && fragmentArray.size() > 0) {
        for (int i = 0; i < getCount(); i++) {
          WeekFragment weekFragment = fragmentArray.get(i);
          if (weekFragment != null) {
            weekFragment.setEnabled(enable);
          }
        }
      }
    }
  }
}