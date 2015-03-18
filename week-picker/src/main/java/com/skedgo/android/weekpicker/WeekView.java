package com.skedgo.android.weekpicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Calendar;

public class WeekView extends LinearLayout {

    private static final String TAG = "LOL";

    private int mSelectedPosition;
    private int mSelectedItem;
    private boolean mIsSelectionVisible = true;

    private DateView[] mDateViews;
    private ValueAnimator mSelectionAnimator;
    private Drawable mSelectionDrawable;
    private Drawable mSelectionCircleDrawable;

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

    public int getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedItem(int selectedItem, boolean hasAnimation) {
        if (selectedItem < 0 || selectedItem >= mDateViews.length) {
            // Out of bounds.
            return;
        }

        if (mSelectedItem != selectedItem) {
            int darkTextColor = getResources().getColor(R.color.day_of_month);
            mDateViews[mSelectedItem].getDayOfMonthView().setTextColor(darkTextColor);
        }

        mSelectedItem = selectedItem;

        if (hasAnimation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            animateToSelectedPosition(mDateViews[mSelectedItem].getLeft());
        } else {
            // Wait until getLeft() gets measured.
            post(new Runnable() {

                @Override
                public void run() {
                    // Cancel any animator.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && mSelectionAnimator != null) {
                        mSelectionAnimator.cancel();
                    }

                    moveToSelectedPosition(mDateViews[mSelectedItem].getLeft());

                    int lightTextColor = getResources().getColor(android.R.color.white);
                    mDateViews[mSelectedItem].getDayOfMonthView().setTextColor(lightTextColor);
                }
            });
        }
    }

    public DateView[] getDateViews() {
        return mDateViews;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIsSelectionVisible) {
            canvas.save();

            canvas.translate(mSelectedPosition, 0.0f);
            mSelectionDrawable.setBounds(0, 0, (getWidth() / 7), getHeight());
            mSelectionDrawable.draw(canvas);

            canvas.restore();

            // Draws the circle.
            canvas.save();

            View dayOfMonthView = mDateViews[0].getDayOfMonthView();
            canvas.translate(
                    mSelectedPosition + ((dayOfMonthView.getWidth() - mSelectionCircleDrawable.getIntrinsicWidth()) / 2),
                    dayOfMonthView.getTop() + ((dayOfMonthView.getHeight() - mSelectionCircleDrawable.getIntrinsicHeight()) / 2)
            );
            mSelectionCircleDrawable.setBounds(
                    0, 0,
                    mSelectionCircleDrawable.getIntrinsicWidth(),
                    mSelectionCircleDrawable.getIntrinsicHeight()
            );
            mSelectionCircleDrawable.draw(canvas);

            canvas.restore();

            Log.d(TAG, "Render selection");
        } else {
            Log.d(TAG, "Ignore rendering selection");
        }
    }

    private void initLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.week_view, this, true);
        initDateViewArray();
        setWillNotDraw(false);

        mSelectionDrawable = getResources().getDrawable(R.drawable.shape_selected_date);
        mSelectionCircleDrawable = getResources().getDrawable(R.drawable.shape_selected_circle);
    }

    private void initDateViewArray() {
        mDateViews = new DateView[Calendar.DAY_OF_WEEK];
        mDateViews[0] = (DateView) findViewById(R.id.dateView0);
        mDateViews[1] = (DateView) findViewById(R.id.dateView1);
        mDateViews[2] = (DateView) findViewById(R.id.dateView2);
        mDateViews[3] = (DateView) findViewById(R.id.dateView3);
        mDateViews[4] = (DateView) findViewById(R.id.dateView4);
        mDateViews[5] = (DateView) findViewById(R.id.dateView5);
        mDateViews[6] = (DateView) findViewById(R.id.dateView6);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animateToSelectedPosition(int newSelectedPosition) {
        // Cancel the previous animator.
        if (mSelectionAnimator != null) {
            mSelectionAnimator.cancel();
        }

        // Perform new animation.
        mSelectionAnimator = ValueAnimator.ofInt(mSelectedPosition, newSelectedPosition);
        mSelectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveToSelectedPosition((Integer) animation.getAnimatedValue());
            }
        });
        mSelectionAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                int lightTextColor = getResources().getColor(android.R.color.white);
                mDateViews[mSelectedItem].getDayOfMonthView().setTextColor(lightTextColor);
            }
        });
        mSelectionAnimator.start();
    }

    private void moveToSelectedPosition(int newSelectedPosition) {
        mSelectedPosition = newSelectedPosition;
        ViewCompat.postInvalidateOnAnimation(this);
    }
}