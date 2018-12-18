package com.example.isurehorizontalscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/*
* 从ViewGroup的注释可知，google推荐阅读 FrameLayout 源码来分析一个自定义ViewGroup的实现。
* 从ViewGroup的注释可知，google提供了一个自定义ViewGroup的demo，本项目的google模块代码就是它的拷贝。
*
* 该控件假设每个子控件的布局文件一样，有点类似于 ListView、RecyclerView，只不过为了减少程序的复杂度，没有实现adapter。
* 因此在实现xml布局文件时，需人工保证每个子控件的layout_width、layout_height、layout_margin*等布局相关属性一样。
* 因为在 onMeasure、onLayout、以及事件处理中，都是默认每个控件的上述相关值是一模一样的。
*
* 考虑到 自身控件宽为wrap_content等各种情况。考虑到 自身控件padding 属性， 考虑到子控件margin属性。
 * 为减少程序复杂度，自身控件padding属性和子控件margin属性 均未考虑start、end（RTL语言环境会用到）。
* */
public class HorizontalScrollViewEx extends ViewGroup {

    public HorizontalScrollViewEx(Context context) {
        this(context, null);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /*
    * 如果本控件的layout_width（或高）为wrap_content，则本控件的宽为：Math.min(父控件宽, Math.max(getSuggestedMinimumWidth(), 第一个子控件的宽))。
    * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
        * 1.先依次完成所有子控件的测量
        *
        * 在测量子控件的过程中，需要考虑那些已测量的子控件对剩余宽高的影响。
        * */
        int childState = 0;
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                /*
                * 实际开发中 widthUsed 和 heightUsed 一般不会同时为0。
                * 这里是为了简化业务逻辑，重点突出自定义ViewGroup的关键步骤和思路，而不是旨在实现业务多复杂的自定义ViewGroup。
                *
                * widthUsed, heightUsed 需要传入其他子控件在水平或垂直方向已经占据的宽/高。
                *
                * ViewGroup的不同子类对于 widthUsed 和 heightUsed 有各自的实现算法，这正是它们之所以存在差别的核心所在。
                * */
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                /*
                * 将所有子控件的 MeasuredState 收集起来，父控件会将子控件的 MeasuredState 加入到自己的 MeasuredState 中，
                * 递归往上报，？？？
                *
                * */
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        /*
         * 2.再根据子控件的测量结果，测量父控件自身。
         *
         * 在实现继承自ViewGroup的自定义控件时最好用 resolveSizeAndState 来生成测量宽高。
         *
         * */
        int maxWidth;
        int maxHeight;

        final View child = getChildAt(0);
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        maxWidth = child.getMeasuredWidth() + getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin;
        maxHeight = child.getMeasuredHeight() + getPaddingTop() + getPaddingRight() + lp.topMargin + lp.bottomMargin;

        //以下代码（直到方法结束）可以当成模板来用，不要改动它们，仅需根据实际需要计算好 maxWidth、maxHeight即可。
        maxWidth = Math.max(getSuggestedMinimumWidth(), maxWidth);
        maxHeight = Math.max(getSuggestedMinimumHeight(), maxHeight);

        /*
        * resolveSizeAndState 方法会根据参数 widthMeasureSpec（ 父容器施加的约束）以及参数 width（控件自身计算后得出的期望尺寸）
        * 来返回合适的值作为测量的宽高；并且会将控件自身和子控件的 MEASURED_STATE_TOO_SMALL 加入到结果中。
        *
        * resolveSizeAndState 没有对第一个参数width做任何处理，也就没有考虑到 getSuggestedMinimumWidth() ，这就是上面两行为什么那样写的原因。
        *
        * */
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState));
    }

    /*
    * 控件自身的布局已经在layout方法中实现了，onLayout方法是用来实现子控件的布局的.
    * 布局子控件时调用子控件的layout方法，而不是onLayout方法。
    *
    * 布局子控件时需要考虑父控件的padding属性和子控件自身的margin属性。
    *
    * 一般情况下，布局子控件时会采用子控件的测量宽/高作为子控件的宽/高。
    *
    * 子控件的left = 父控件的padding_left + 子控件的layout_marginLeft + [其他已布局子控件的影响]
    * 子控件的top = 父控件的padding_top + 子控件的layout_marginTop + [其他已布局子控件的影响]
    * 子控件的right = 子控件的left + 子控件的getMeasuredWidth();
    * 子控件的bottom = 子控件的top + 子控件的getMeasuredHeight();
    * 注意：[其他已布局子控件的影响]，不同的ViewGroup子类有不同的实现。例如：LinearLayout水平布局时，
    * 子控件的top就不需要考虑其他子控件的影响，子控件的left则需要考虑；而LinearLayout垂直布局时，则正好相反。
    * */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left;
        int top;
        int right;
        int bottom;

        final View firstChild = getChildAt(0);
        final MarginLayoutParams lp = (MarginLayoutParams) firstChild.getLayoutParams();
        top = getPaddingTop() + lp.topMargin;
        bottom = top + firstChild.getMeasuredHeight();

        int containSingleChildWidth = lp.leftMargin + firstChild.getMeasuredWidth() + lp.rightMargin;

        final int size = getChildCount();
        for (int i = 0; i < size; i++) {
            final View child = getChildAt(i);
            if(child.getVisibility() == View.GONE)
                return;

            left = getPaddingLeft() + lp.leftMargin + containSingleChildWidth * i;
            right = left + firstChild.getMeasuredWidth();

            child.layout(left, top, right, bottom);
        }
    }

    // ----------------------------------------------------------------------
    // The rest of the implementation is for custom per-child layout parameters.
    /*
    * 以下为实现继承自ViewGroup的自定义控件特有，用于解析子控件的布局参数。
    *
    * ViewGroup.LayoutParams是ViewGroup的默认实现，仅仅解析layout_width、layout_height属性。
    * ViewGroup.MarginLayoutParams是ViewGroup.LayoutParams的子类，添加了 layout_margin* 相关属性的解析。
    *
    * 对于ViewGroup的不同子类，有LayoutParams的子类。
    * 例如，AbsoluteLayout有自己的LayoutParams子类，它会添加X和Y值。
    *
    * isure踩过的坑：本控件的LayoutParams只是简单继承自ViewGroup.MarginLayoutParams，并没有添加自定义的布局属性。
    * 但即便如此以下代码也是必须实现的，不然在执行到 (MarginLayoutParams) child.getLayoutParams() 时会报ANR错误。
    * */

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams{
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            /*
            * 添加ViewGroup子类的自定义属性的解析。
            * layout_width、layout_height、layout_margin*已经由MarginLayoutParams父类解析，这里不需要再解析它们。
            */
            //...
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
