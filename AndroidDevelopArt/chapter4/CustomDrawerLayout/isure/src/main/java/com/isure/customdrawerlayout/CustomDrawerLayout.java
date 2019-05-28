package com.isure.animator.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class CustomDrawerLayout extends FrameLayout {
    private static final String TAG = "CustomDrawerLayout";

    public CustomDrawerLayout(@NonNull Context context) {
        this(context, null);
    }

    public CustomDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // DrawerLayout有且仅有两个子控件，第一个子控件是主界面，第二个控件是菜单界面。否则抛出异常。
        if(getChildCount() != 2) {
            throw new RuntimeException("CustomDrawerLayout must be contains two children");
        }

        int childState = 0;
        //测量第一个子控件
        measureChildWithMargins(getChildAt(0), widthMeasureSpec, 0, heightMeasureSpec, 0);
        childState = combineMeasuredStates(childState, getChildAt(0).getMeasuredState());
        /*
        * 测量第二个子控件
        *
        * widthUsed设置为80，是因为菜单界面宽度不会填满父控件，右边留80px。
        * 这里为了简化计算逻辑，直接设置右边留80px，实际项目中，应该是在dimens.xml中配置(以dp为单位)，在此处将dp转换为px。
        * */
        measureChildWithMargins(getChildAt(1), widthMeasureSpec, 80, heightMeasureSpec, 0);
        childState = combineMeasuredStates(childState, getChildAt(1).getMeasuredState());

        //下面测量控件自身
        int maxWidth;
        int maxHeight;

        final View child = getChildAt(0);
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        maxWidth = child.getMeasuredWidth() + getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin;
        maxHeight = child.getMeasuredHeight() + getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin;

        maxWidth = Math.max(getSuggestedMinimumWidth(), maxWidth);
        maxHeight = Math.max(getSuggestedMinimumHeight(), maxHeight);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //布局第一个子控件，主界面
        int childLeft;
        int childTop;
        int childRight;
        int childBottom;

        View child = getChildAt(0);
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        childLeft = getPaddingLeft() + lp.leftMargin;
        childTop = getPaddingTop() + lp.topMargin;
        childRight = childLeft + child.getMeasuredWidth();
        childBottom = childTop + child.getMeasuredHeight();

        child.layout(childLeft, childTop, childRight, childBottom);

        //布局第二个子控件，菜单界面
        child = getChildAt(1);
        lp = (MarginLayoutParams) child.getLayoutParams();
        //菜单界面最初状态是完全隐藏，随着手指向右滑动，一点点向右滑动。
        childLeft =  - child.getMeasuredWidth() - getPaddingRight() - lp.rightMargin + mMenuScrollPosition;
        childTop = getPaddingTop() + lp.topMargin;
        childRight = - getPaddingRight() - lp.rightMargin + mMenuScrollPosition;
        childBottom = childTop + child.getMeasuredHeight();

        child.layout(childLeft, childTop, childRight, childBottom);

        // 设置Menu界面最大滚动值
        mMenuScrollPositionMax = child.getMeasuredWidth() + getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin;
        Log.d(TAG, "onLayout: mMenuScrollPositionMax = "+ mMenuScrollPositionMax);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private int mMenuScrollPositionMax;
    private int mMenuScrollPosition = 0;

    public int getMenuScrollPosition() {
        return mMenuScrollPosition;
    }

    public void setMenuScrollPosition(int menuScrollPosition) {
        // menuScrollPosition取值范围：0 =< menuScrollPosition =< mMenuScrollPositionMax
        menuScrollPosition = Math.max(0, Math.min(menuScrollPosition, mMenuScrollPositionMax));

        mMenuScrollPosition = menuScrollPosition;

        requestLayout();    //请求重新布局
    }

    //*************** 响应触摸事件

    private boolean isMenuScroll = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        Log.d(TAG, "onInterceptTouchEvent: x = " + x);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(x < 20.0f) {
                    isMenuScroll = true;
                } else {
                    isMenuScroll = false;
                }
                break;
        }
        Log.d(TAG, "onInterceptTouchEvent: isMenuScroll = " + isMenuScroll);
        return isMenuScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        Log.d(TAG, "onTouchEvent: action =  " + event.getAction() + ", x = " + x);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(x < 20.0f) {
                    isMenuScroll = true;
                } else {
                    isMenuScroll = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                setMenuScrollPosition((int)x);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //1920为屏幕宽度
                if(x < 1920/2) {
                    setMenuScrollPosition(0);
                } else {
                    Log.d(TAG, "onTouchEvent: action =  " + event.getAction() + ", mMenuScrollPositionMax = " + mMenuScrollPositionMax);
                    setMenuScrollPosition(mMenuScrollPositionMax);
                }
                break;
        }
        return isMenuScroll;
    }
}
