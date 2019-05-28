package com.isure.animation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView mText;
    private Button mBtn;

    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (TextView) findViewById(R.id.text);
        mBtn = (Button) findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                i += 1;
                Log.d(TAG, "onClick: i = " + i);
                if(i%2 != 0) {
                    Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.animation_test);
                    mBtn.startAnimation(animation);
                }else {
                    mBtn.clearAnimation();
                    mBtn.setVisibility(View.GONE);
                }
            }
        });
    }
}
