package com.dove.readandroid.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/3/12 13:49
 * ===============================
 */
public class NoAutoSlideScrollView extends NestedScrollView {
    public NoAutoSlideScrollView(Context context) {
        super(context);
    }

    public NoAutoSlideScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoAutoSlideScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }


}
