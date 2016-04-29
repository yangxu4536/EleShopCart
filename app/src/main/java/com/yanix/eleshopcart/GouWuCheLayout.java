package com.yanix.eleshopcart;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义购物车布局，使用ViewDragHelper来处理拖动
 */
public class GouWuCheLayout extends ViewGroup {
    private final ViewDragHelper mDragHelper;

    private View mDescView;
    private View mBackground;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private float mLastYIntercept;

    private int mDragRange;
    private int mTop;
    private float mDragOffset;


    public GouWuCheLayout(Context context) {
        this(context, null);
    }

    public GouWuCheLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GouWuCheLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDescView = findViewById(R.id.desc);
        mBackground = findViewById(R.id.background);
        mBackground.setAlpha(0.0f);
    }

    public void maximize() {
        smoothSlideTo(0f);
    }

    public void minimize() {
        smoothSlideTo(1f);
    }

    public boolean isBottom() {
        return mDragOffset == 1;
    }

    boolean smoothSlideTo(float slideOffset) {
        final int topBound = getHeight() - mDescView.getMeasuredHeight();
        int y = (int) (topBound + slideOffset * mDragRange);

        if (mDragHelper.smoothSlideViewTo(mDescView, mDescView.getLeft(), y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mDescView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mTop = top;

            mDragOffset = (float) (mDragRange - getBottom() + top) / mDragRange;
            mBackground.setAlpha(1 - mDragOffset);
            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top = getHeight() - mDescView.getMeasuredHeight();
            if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
                top += mDragRange;
            }
            mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
            invalidate();
        }

        public int getViewVerticalDragRange(View child) {
            return mDragRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getHeight() - mDescView.getMeasuredHeight();
            final int bottomBound = getHeight();

            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;

        float y = ev.getY();

        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            intercepted = false;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mLastYIntercept != 0 && y-mLastYIntercept > 0 && !((MaxHeightListView)mDescView).canChildScrollUp()) {
                intercepted = true;
            } else {
                intercepted = false;
            }
            return intercepted;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            mDragHelper.cancel();
            intercepted = false;
            break;
        }

        mLastYIntercept = y;

        return mDragHelper.shouldInterceptTouchEvent(ev) || intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);

        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        boolean isHeaderViewUnder = mDragHelper.isViewUnder(mDescView, (int) x, (int) y);
        boolean isBackgroundUnder = isViewHit(mBackground, (int) x, (int) y) && mBackground.getAlpha() > 0.0f;
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                final float dx = x - mInitialMotionX;
                final float dy = y - mInitialMotionY;
                final int slop = mDragHelper.getTouchSlop();
                boolean isTap = dx * dx + dy * dy < slop * slop;
                if (isBackgroundUnder && !isHeaderViewUnder && isTap) {
                    minimize();
                }
                break;
            }
        }

        return isHeaderViewUnder && isViewHit(mDescView, (int) x, (int) y) || isBackgroundUnder;
    }

    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mDragRange = mDescView.getMeasuredHeight();

        int offset = 0;

        if (mTop <= 0) {
            mTop = b;
            mDragOffset = 1;
        }

        mBackground.layout(
                l,
                t,
                r,
                b);

        mDescView.layout(
                0,
                mTop,
                r,
                b + offset);
    }
}
