package com.skedgo.android.weekpicker;

import android.content.Context;
import androidx.core.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A {@link ViewPager} whose swipe-ability can be disabled via {@link ViewPager#setEnabled(boolean)}.
 */
public class SwipeDisableViewPager extends ViewPager {
  public SwipeDisableViewPager(Context context) {
    super(context);
  }

  public SwipeDisableViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return isEnabled() && super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    return isEnabled() && super.onTouchEvent(ev);
  }
}