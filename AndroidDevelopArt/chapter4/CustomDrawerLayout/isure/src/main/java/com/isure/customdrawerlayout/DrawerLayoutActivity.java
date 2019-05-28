package com.isure.customdrawerlayout;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.isure.animator.drawer.CustomDrawerLayout;

public class DrawerLayoutActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtnScroll;
    private CustomDrawerLayout mCustomDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        mBtnScroll = (Button) findViewById(R.id.btn_scroll);
        mCustomDrawerLayout = (CustomDrawerLayout) findViewById(R.id.drawer_layout);

        mBtnScroll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // 1840 = 1920(屏幕宽度) - 80(menu界面与屏幕右边的距离)
        ObjectAnimator.ofInt(mCustomDrawerLayout, "menuScrollPosition", 3760)
                .setDuration(1000)
                .start();
    }
}
