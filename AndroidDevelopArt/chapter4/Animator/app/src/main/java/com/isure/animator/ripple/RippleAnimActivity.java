package com.isure.animator.ripple;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.isure.animator.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RippleAnimActivity extends AppCompatActivity {

    private RippleView mRippleView;

    private AnimatorSet mAnimatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple_anim);

        mRippleView = (RippleView) findViewById(R.id.ripple_view);
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                startWidthAnim();
//                startAlphaAnim();
//                startAnimSet();
                startCycleAnimSet();
            }
        });
    }

    private void startWidthAnim() {
        ObjectAnimator.ofFloat(mRippleView, "rippleWidth", 360)
                .setDuration(2000)
                .start();
    }

    private void startAlphaAnim() {
        ObjectAnimator.ofFloat(mRippleView, "alpha", 1, 0)
                .setDuration(2000)
                .start();
    }

    private void startAnimSet() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mRippleView, "rippleWidth", 0, 360),
                ObjectAnimator.ofFloat(mRippleView, "alpha", 1, 0));
        set.setDuration(2000).start();
    }

    private void startCycleAnimSet() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mRippleView, "rippleWidth", 0, 360);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mRippleView, "alpha", 1, 0);
        animator2.setRepeatCount(ValueAnimator.INFINITE);

        List<Animator> animatorList = new ArrayList<>();
        animatorList.add(animator1);
        animatorList.add(animator2);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animatorList);
        mAnimatorSet.setDuration(2000).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAnimatorSet.cancel();
    }
}
