package com.yanix.eleshopcart;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ListView;

public class MaxHeightListView extends ListView {
    // listview最大高度
    private int listViewHeight;

    public MaxHeightListView(Context context) {
        super(context);
    }

    public MaxHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightListView);
        // 默认最大高度400dp
        listViewHeight = a.getDimensionPixelSize(R.styleable.MaxHeightListView_maxHeight, Tools.dip2px(context, 400));
        a.recycle();
    }

    public MaxHeightListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (listViewHeight > -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(listViewHeight,
                    MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 判断listview是否滚动到了最顶部
     */
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return getChildCount() > 0 && (getFirstVisiblePosition() > 0 || getChildAt(0).getTop() < getPaddingTop());
        } else {
            return ViewCompat.canScrollVertically(this, -1) || this.getScrollY() > 0;
        }
    }
}