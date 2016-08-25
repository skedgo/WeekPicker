package com.skedgo.android.weekpicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.Calendar;

public class WeekView extends LinearLayout {
  private int selectedPosition;
  private int selectedItem;
  private DateView[] dateViews;
  private ValueAnimator selectionAnimator;
  private Drawable selectionDrawable;
  private Drawable selectionCircleDrawable;
  @ColorInt private int selectedTextColor;

  public WeekView(Context context) {
    super(context);
    initLayout();
  }

  public WeekView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initLayout();
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public WeekView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initLayout();
  }

  public void setSelectedItem(int selectedItem, boolean hasAnimation) {
    if (selectedItem < 0 || selectedItem >= dateViews.length) {
      // Out of bounds.
      return;
    }

    if (this.selectedItem != selectedItem) {
      int darkTextColor = getResources().getColor(R.color.wp_dayOfMonthText);
      dateViews[this.selectedItem].getDayOfMonthView().setTextColor(darkTextColor);
    }

    this.selectedItem = selectedItem;

    if (hasAnimation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      animateToSelectedPosition(dateViews[this.selectedItem].getLeft());
    } else {
      // Wait until getLeft() gets measured.
      post(new Runnable() {
        @Override
        public void run() {
          // Cancel any animator.
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && selectionAnimator != null) {
            selectionAnimator.cancel();
          }

          moveToSelectedPosition(dateViews[WeekView.this.selectedItem].getLeft());
          dateViews[WeekView.this.selectedItem].getDayOfMonthView().setTextColor(selectedTextColor);
        }
      });
    }
  }

  public DateView[] getDateViews() {
    return dateViews;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.save();

    canvas.translate(selectedPosition, 0.0f);
    selectionDrawable.setBounds(0, 0, (getWidth() / 7), getHeight());
    selectionDrawable.draw(canvas);

    canvas.restore();

    // Draws the circle.
    canvas.save();

    final DateView dateView = dateViews[selectedItem];
    final int width = dateView.getWidth();
    final int bottom = dateView.getWrapperView().getBottom();
    final int dx = selectedPosition + ((width - selectionCircleDrawable.getIntrinsicWidth()) / 2);
    final int dy = bottom - selectionCircleDrawable.getIntrinsicHeight();
    canvas.translate(dx, dy);
    selectionCircleDrawable.setBounds(
        0, 0,
        selectionCircleDrawable.getIntrinsicWidth(),
        selectionCircleDrawable.getIntrinsicHeight()
    );
    selectionCircleDrawable.draw(canvas);

    canvas.restore();
  }

  private void initLayout() {
    LayoutInflater.from(getContext()).inflate(R.layout.week_view, this, true);
    initDateViewArray();
    setWillNotDraw(false);

    selectionDrawable = getResources().getDrawable(R.drawable.shape_selected_rect);
    selectionCircleDrawable = getResources().getDrawable(R.drawable.shape_selected_circle);
    selectedTextColor = getResources().getColor(R.color.wp_selectedText);
  }

  private void initDateViewArray() {
    dateViews = new DateView[Calendar.DAY_OF_WEEK];
    dateViews[0] = (DateView) findViewById(R.id.dateView0);
    dateViews[1] = (DateView) findViewById(R.id.dateView1);
    dateViews[2] = (DateView) findViewById(R.id.dateView2);
    dateViews[3] = (DateView) findViewById(R.id.dateView3);
    dateViews[4] = (DateView) findViewById(R.id.dateView4);
    dateViews[5] = (DateView) findViewById(R.id.dateView5);
    dateViews[6] = (DateView) findViewById(R.id.dateView6);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void animateToSelectedPosition(int newSelectedPosition) {
    // Cancel the previous animator.
    if (selectionAnimator != null) {
      selectionAnimator.cancel();
    }

    // Perform new animation.
    selectionAnimator = ValueAnimator.ofInt(selectedPosition, newSelectedPosition);
    selectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        moveToSelectedPosition((Integer) animation.getAnimatedValue());
      }
    });
    selectionAnimator.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        dateViews[selectedItem].getDayOfMonthView().setTextColor(selectedTextColor);
      }
    });
    selectionAnimator.start();
  }

  private void moveToSelectedPosition(int newSelectedPosition) {
    selectedPosition = newSelectedPosition;
    ViewCompat.postInvalidateOnAnimation(this);
  }
}