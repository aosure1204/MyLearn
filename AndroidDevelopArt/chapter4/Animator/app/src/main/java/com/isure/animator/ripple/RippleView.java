package com.isure.animator.ripple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.isure.animator.R;

public class RippleView extends View {
    private static final String TAG = "RippleView";

    private static final float DEFAULT_INNER_RADIUS = 200;
    private static final float DEFAULT_RIPPLE_WIDTH = 400;
    private static final int DEFAULT_RIPPLE_COLOR = 0xff0000;

    private float mInnerRadius;
    private float mRippleWidth;
    private int mRippleColor;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RippleView, defStyleAttr, defStyleRes);

        mInnerRadius = a.getFloat(R.styleable.RippleView_innerRadius, DEFAULT_INNER_RADIUS);
        mRippleWidth = a.getFloat(R.styleable.RippleView_rippleWidth, DEFAULT_RIPPLE_WIDTH);
        mRippleColor = a.getColor(R.styleable.RippleView_rippleColor, DEFAULT_RIPPLE_COLOR);

        a.recycle();
    }

    public float getRippleWidth() {
        return mRippleWidth;
    }

    public void setRippleWidth(float rippleWidth) {
        mRippleWidth = rippleWidth;
        requestLayout();
    }

/*    public int getRippleColor(){
        return  mRippleColor;
    }

    public void setRippleColor(int rippleColor) {
        mRippleColor = rippleColor;
        invalidate();
    }*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //如果宽或高尺寸明确，直接将 MeasureSpec.getSize(widthMeasureSpec) 值作为测量宽或高。
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            /* 此处为当 layout_width = "wrap_content" 时，控件为显示内容需要的最小宽度。
             * 注意：宽高要将 padding 考虑在内。
             */
            /*
             * 不同控件，修改此处代码即可，此方法中其他代码可以当成模板来用，不要改动其他代码。
             * */
            width = (int)((mInnerRadius + mRippleWidth) * 2 + 0.5f);

            width += getPaddingLeft() + getPaddingRight();

            /*
             * getSuggestedMinimumHeight()/getSuggestedMinimumWidth()，
             * 考虑到 android:minWidth/minHeight 属性设置的最小宽高和 android:background 属性设置的背景图片大小。
             * */
            width = Math.max(width, getSuggestedMinimumWidth());
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(widthSize, width);
            }
        }

        if (heightMode== MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            /*
             * 不同控件，修改此处代码即可，此方法中其他代码可以当成模板来用，不要改动其他代码。
             * */
            height = (int)((mInnerRadius + mRippleWidth)  * 2 + 0.5f);

            height += getPaddingTop() + getPaddingBottom();

            height = Math.max(height, getSuggestedMinimumHeight());
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(heightSize, height);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(mRippleColor);
        canvas.drawCircle(getWidth()/2, getHeight() /2 , mRippleWidth + mInnerRadius, paint);   //绘制一个实心圆
    }
}
