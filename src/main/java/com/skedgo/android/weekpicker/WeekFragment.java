package com.skedgo.android.weekpicker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class WeekFragment extends Fragment {
  public static final String KEY_SELECTED_DATE = "selectedDate";
  public static final String KEY_DATE_RANGE = "dateRange";

  private Calendar mSelectedDate;
  private ArrayList<Date> mDateRange;
  private SimpleDateFormat mDayOfWeekFormat;
  private SimpleDateFormat mDayOfMonthFormat;
  private boolean enabled = true;

  private WeekView mWeekView;

  private OnDateSelectedListener mOnDateSelectedListener;

  public WeekFragment() {
    mDayOfWeekFormat = new SimpleDateFormat("EEE");
    mDayOfMonthFormat = new SimpleDateFormat("d");
  }

  public static WeekFragment newInstance(Calendar selectedDate, int weekStart) {
    Bundle args = new Bundle();
    args.putSerializable(KEY_SELECTED_DATE, selectedDate);

    ArrayList<Date> dateRange = getDateRangeWithWeekStart(selectedDate, weekStart);
    args.putSerializable(KEY_DATE_RANGE, dateRange);

    WeekFragment fragment = new WeekFragment();
    fragment.setArguments(args);
    fragment.mSelectedDate = selectedDate;
    fragment.mDateRange = dateRange;
    return fragment;
  }

  public static WeekFragment newInstance(Calendar selectedDate) {
    Bundle args = new Bundle();
    args.putSerializable(KEY_SELECTED_DATE, selectedDate);

    ArrayList<Date> dateRange = getDateRange(selectedDate);
    args.putSerializable(KEY_DATE_RANGE, dateRange);

    WeekFragment fragment = new WeekFragment();
    fragment.setArguments(args);
    fragment.mSelectedDate = selectedDate;
    fragment.mDateRange = dateRange;
    return fragment;
  }

  protected static ArrayList<Date> getDateRangeWithWeekStart(Calendar selectedDate, int weekStart) {
    ArrayList<Date> dateRange = new ArrayList<Date>();
    Calendar calendar = (Calendar) selectedDate.clone();
    calendar.set(Calendar.DAY_OF_WEEK, convertJodaTimeToCalendar(weekStart));
    for (int i = 0; i < Calendar.DAY_OF_WEEK; i++) {
      Date date = calendar.getTime();
      dateRange.add(date);
      calendar.add(Calendar.DATE, 1);
    }

    return dateRange;
  }

  protected static ArrayList<Date> getDateRange(Calendar selectedDate) {
    ArrayList<Date> dateRange = new ArrayList<Date>();
    Calendar calendar = (Calendar) selectedDate.clone();
    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
    for (int i = 0; i < Calendar.DAY_OF_WEEK; i++) {
      Date date = calendar.getTime();
      dateRange.add(date);
      calendar.add(Calendar.DATE, 1);
    }

    return dateRange;
  }

  private static int convertJodaTimeToCalendar(int date) {
    return date % 7 + 1;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Restore models after recreating the Activity.
    if (savedInstanceState != null) {
      mSelectedDate = (Calendar) getArguments().getSerializable(KEY_SELECTED_DATE);
      mDateRange = (ArrayList<Date>) getArguments().getSerializable(KEY_DATE_RANGE);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mWeekView = (WeekView) inflater.inflate(R.layout.fragment_week, container, false);
    return mWeekView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    presentDates();
  }

  public Date getSelectedDate() {
    return mSelectedDate.getTime();
  }

  public void setSelectedDate(Date selectedDate) throws IllegalArgumentException {
    /**compare by Date.before() Date.after() will compare the times, which triggers wrong Exception on boundary cases**/
    int selectedJulian = getJulianDay(selectedDate);
    int wsJulian = getJulianDay(mDateRange.get(0));
    int wkJulian = getJulianDay(mDateRange.get(6));
    if (selectedJulian < wsJulian || selectedJulian > wkJulian) {
      // FIXME: Why is it out of date range???
      // throw new IllegalArgumentException("Out of date range");
      Log.e(WeekFragment.class.getSimpleName(), "Out of date range");
      return;
    }

    mSelectedDate.setTime(selectedDate);
    emitSelectedDate();

    showSelectedDate(selectedDate);
  }

  public int getJulianDay(Date weekstart) {
    int weekStartJulian = Time.getJulianDay(weekstart.getTime(), TimeZone.getDefault().getOffset(weekstart.getTime()) / 1000);
    return weekStartJulian;
  }

  public void setOnDateSelectedListener(OnDateSelectedListener listener) {
    mOnDateSelectedListener = listener;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    presentDates();
  }

  protected void showSelectedDate(Date selectedDate) {
    // If the WeekView does exist at this point, update it to reflect selection.
    if (mWeekView != null) {
      // Find selected item by comparing dates.
      for (int i = 0; i < Calendar.DAY_OF_WEEK; i++) {
        // Should only compare the date value, not the time.
        // Note that using equals() is to compare to the time level.
        boolean isSameDay = ApacheDateUtils.isSameDay(selectedDate, mDateRange.get(i));
        if (isSameDay) {
          mWeekView.setSelectedItem(i, true);
          break;
        }
      }
    }
  }

  /**
   * Emit latest selected date to subscribers.
   */
  protected void emitSelectedDate() {
    if (mOnDateSelectedListener != null) {
      mOnDateSelectedListener.onDateSelected(getSelectedDate());
    }
  }

  protected void presentDates() {
    if (mWeekView == null) {
      return;
    }

    for (int i = 0; i < Calendar.DAY_OF_WEEK; i++) {
      final Date date = mDateRange.get(i);

      DateView dateView = mWeekView.getDateViews()[i];
      dateView.setEnabled(enabled);

      // Show day of week.
      String dayOfWeekText = mDayOfWeekFormat.format(date).toUpperCase();
      dateView.getDayOfWeekView().setText(dayOfWeekText);

      // Show day of month.
      String dayOfMonthText = mDayOfMonthFormat.format(mDateRange.get(i));
      dateView.getDayOfMonthView().setText(dayOfMonthText);

      dateView.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
          setSelectedDate(date);
        }
      });

      // Present the selected date. We just need the date to be equal, not the time part.
      if (ApacheDateUtils.isSameDay(date, mSelectedDate.getTime())) {
        mWeekView.setSelectedItem(i, false);
      }

      ImageView todayView = dateView.getTodayView();
      if (DateUtils.isToday(date.getTime())) {
        todayView.setVisibility(View.VISIBLE);
        todayView.setImageResource(R.drawable.shape_today_circle);
      } else {
        todayView.setVisibility(View.GONE);
      }
    }
  }

  /**
   * Credit to source code of https://commons.apache.org/proper/commons-lang/javadocs/api-2.6/org/apache/commons/lang/time/DateUtils.html
   */
  public static class ApacheDateUtils {

    /**
     * <p>Checks if two date objects are on the same day ignoring time.</p>
     * <p/>
     * <p>28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true.
     * 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return false.
     * </p>
     *
     * @param date1 the first date, not altered, not null
     * @param date2 the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either date is <code>null</code>
     * @since 2.1
     */
    public static boolean isSameDay(Date date1, Date date2) {
      if (date1 == null || date2 == null) {
        throw new IllegalArgumentException("The date must not be null");
      }

      Calendar cal1 = Calendar.getInstance();
      cal1.setTime(date1);
      Calendar cal2 = Calendar.getInstance();
      cal2.setTime(date2);
      return isSameDay(cal1, cal2);
    }

    /**
     * <p>Checks if two calendar objects are on the same day ignoring time.</p>
     * <p/>
     * <p>28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true.
     * 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return false.
     * </p>
     *
     * @param cal1 the first calendar, not altered, not null
     * @param cal2 the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     * @since 2.1
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
      if (cal1 == null || cal2 == null) {
        throw new IllegalArgumentException("The date must not be null");
      }

      return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
          cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
          cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
  }
}