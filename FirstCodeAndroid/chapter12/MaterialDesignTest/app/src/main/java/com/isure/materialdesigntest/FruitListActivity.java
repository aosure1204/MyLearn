package com.isure.materialdesigntest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FruitListActivity extends AppCompatActivity implements View.OnClickListener{

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private FloatingActionButton mFloatingBtn;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Fruit[] fruits = {new Fruit("Apple", R.drawable.apple), new Fruit("Banana", R.drawable.banana),
            new Fruit("Orange", R.drawable.orange), new Fruit("Watermelon", R.drawable.watermelon),
            new Fruit("Pear", R.drawable.pear), new Fruit("Grape", R.drawable.grape),
            new Fruit("Pineapple", R.drawable.pineapple), new Fruit("Strawberry", R.drawable.strawberry),
            new Fruit("Cherry", R.drawable.cherry), new Fruit("Mango", R.drawable.mango)};

    private List<Fruit> mFruitList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private FruitAdapter mFruitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_list);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mNavView = (NavigationView)findViewById(R.id.nav_view);
        mNavView.setCheckedItem(R.id.nav_call);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        mFloatingBtn = (FloatingActionButton) findViewById(R.id.floating_btn);
        mFloatingBtn.setOnClickListener(this);

        initFruits();
        initRecycleView();

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
    }

    private void refreshFruits(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initFruits();
                            mFruitAdapter.notifyDataSetChanged();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        }).start();
    }

    // 有更简洁的方式，见 下一个未注释的 initData方法。
/*
    private void initData() {
        int[] imageResIds = new int[] {

        };
        String[] nameStr = new String[] {

        };

        for(int i=0; i<imageResIds.length; i++) {
            Fruit fruit = new Fruit(nameStr[i], imageResIds[i]);
            mFruitList.add(fruit);
        }
    }
*/

    private void initFruits() {
        mFruitList.clear();
        for(int i=0; i<50; i++) {
            Random random = new Random();
            int index = random.nextInt(fruits.length);
            mFruitList.add(fruits[index]);
        }
    }

    private void initRecycleView() {
        mFruitAdapter = new FruitAdapter(mFruitList, this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mFruitAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_backup:
                Toast.makeText(this, "you clicked backup item", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_delete:
                Toast.makeText(this, "You clicked delete item", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_settings:
                Toast.makeText(this, "You clicked settings item", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_btn:
//                Toast.makeText(this, "you clicked FloatingActionButton", Toast.LENGTH_SHORT).show();
                Snackbar.make(v, "Data deleted", Snackbar.LENGTH_SHORT)
                        .setAction("undo", new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                //do something.
                                Toast.makeText(FruitListActivity.this, "Data restored", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                break;
        }
    }
}
