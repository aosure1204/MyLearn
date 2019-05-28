package com.example.isurecircleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/*
* 完全由isure原创，没有参考“Android开发艺术探索”中实例，因此与本项目book模块的CircleView实现思路完全不同。
*
* 实现继承自View的自定义控件，需要实现 onMeasure 和 onDraw 方法；onLayout方法不需要实现，因为控件本身
* 的布局在 layout 方法中完成，onLayout 方法是用来布局子控件的。
* 在 onMeasure 和 onDraw 方法中需要将 padding 考虑在内，padding 将影响控件本身的测量宽高以及内容的绘制位置。
*
* 自定义控件支持添加自己的属性
* 1.属性文件添加在 res/values/attrs.xml 中，格式如下：
* <resources>
    <declare-styleable name="CircleView">
        <attr name="radius" format="dimension" />
    </declare-styleable>
</resources>
* 2.在构造函数中加载并解析属性文件，代码如下：
*         final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CircleView, defStyleAttr, defStyleRes);

        mRadius = a.getDimensionPixelSize(R.styleable.CircleView_radius, mRadius);

        a.recycle();
* 3.在布局文件中添加自定义控件
* <LinearLayout
*   xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="500px"
    android:layout_height="500px">

    <com.example.isurecircleview.CircleView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#ff00ff"
        android:paddingLeft="100px"
        android:paddingTop="100px"
        android:paddingRight="200px"
        android:paddingBottom="200px"
        app:radius="100px" />
  </LinearLayout>
* 4.注意要在布局根控件的开始位置添加 xmlns:app="http://schemas.android.com/apk/res-auto" ，这样就可以使用app命名空间了。
* <LinearLayout
    xmlns:app="http://schemas.android.com/apk/res-auto"

    <com.example.isurecircleview.CircleView
        app:radius="100px" />
  </LinearLayout>
* */
public class CircleView extends View {

    private int mRadius = 0; // TODO: use a default from R.dimen...

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CircleView, defStyleAttr, defStyleRes);

        mRadius = a.getDimensionPixelSize(R.styleable.CircleView_radius, mRadius);

        a.recycle();
    }

    //Consider wrap_content， padding and radius。
    /*
    * 此方法的实现参考了TextView的onMeasure方法实现。
    *
    * 此 onMeasure 可以作为实现继承自View的自定义控件的模板方法，仅需修改以下两行即可，其他代码不要随意修改：
    * 1.width = mRadius * 2;
    * 2.height = mRadius * 2;
    *
    * 已经将控件自身的 layout_width/height 的各种取值都考虑进来，也就是说，无论设置 layout_width = "wrap_content"
    * ,layout_width="match_parent" 或 layout_width="具体尺寸值"，都能达到想要的效果。
    *
    * 需要将控件自身的  padding 属性考虑在内，padding 将影响控件本身的测量宽高。
    *
    * 控件自身的 layout_margin 相关属性由父容器考虑。
    *
    * */
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
            width = mRadius * 2;

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
            height = mRadius * 2;

            height += getPaddingTop() + getPaddingBottom();

            height = Math.max(height, getSuggestedMinimumHeight());
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(heightSize, height);
            }
        }

        setMeasuredDimension(width, height);
    }

    /*
    * 我的绘制策略：如果getWidth < getPaddingLeft + getPaddingRight + mRadius * 2， 则将圆的半径mRadius减小使左边表达式相等为止，
    * 有可能mRadius <= 0 ,则不绘制圆。
    *
    * 宽和高计算后的 mRadius 可能不同，取两者中最小值。
    *
    * 绘制时需要将 padding 考虑在内，padding 影响绘制内容在控件自身坐标系的位置。
     * */
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int radiusX = mRadius;
        int radiusY= mRadius;

        int resultRadius;

        if(contentWidth < mRadius * 2) {
            radiusX = contentWidth / 2;
        }

        if(contentHeight < mRadius * 2) {
            radiusY = contentHeight / 2;
        }

        resultRadius = Math.min(radiusX, radiusY);

        //resultRadius <= 0, 不绘制圆
        if(resultRadius <= 0) {
            return;
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        canvas.drawCircle(paddingLeft + resultRadius, paddingTop + resultRadius, resultRadius, paint);
    }
}
