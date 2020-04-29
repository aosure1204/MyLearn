package com.isure.imageloaderdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.isure.imageloaderdemo.loader.BitmapLoader;
import com.isure.imageloaderdemo.utils.MyUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
/*
* 本项目来自《Android开发艺术探索》(第12章 Bitmap的加载和Cache)

        本项目包含isure和book两个模块，isure主要来自book，但细节上可能略有不同，工作中应参考book。

        本项目涉及的知识点：
        1.图片压缩技术，BitmapFactory
        2.缓存技术，内存缓存LruCache，磁盘缓存DiskLruCache
        3.网络编程，HttpURLConnection
        4.线程池，ThreadPoolExecutor
        5.Handler，先在子线程中执行加载图片的任务，后通过Handler更新UI。
        6.数据加密，MessageDigest，对url进行加密
        7.GridView和Adapter
        8.其他
        获取联网状态，获取屏幕尺寸。
* */
public class MainActivity extends AppCompatActivity implements GridView.OnScrollListener{

    private List<String> mUrlList = new ArrayList<String>();
    private BitmapLoader mBitmapLoader;
    private ImageAdapter mImageAdapter;

    private boolean isGridViewIdle = true;
    private boolean isWifi = false;
    private boolean mCanGetBitmapFromNetWork = false;
    private int mImageWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBitmapLoader = new BitmapLoader(this);
        initData();
        initView();
    }

    private void initData() {
        String[] imageUrls = {
                "http://b.hiphotos.baidu.com/zhidao/pic/item/a6efce1b9d16fdfafee0cfb5b68f8c5495ee7bd8.jpg",
                "http://pic47.nipic.com/20140830/7487939_180041822000_2.jpg",
                "http://pic41.nipic.com/20140518/4135003_102912523000_2.jpg",
                "http://img2.imgtn.bdimg.com/it/u=1133260524,1171054226&fm=21&gp=0.jpg",
                "http://h.hiphotos.baidu.com/image/pic/item/3b87e950352ac65c0f1f6e9efff2b21192138ac0.jpg",
                "http://pic42.nipic.com/20140618/9448607_210533564001_2.jpg",
                "http://pic10.nipic.com/20101027/3578782_201643041706_2.jpg",
                "http://picview01.baomihua.com/photos/20120805/m_14_634797817549375000_37810757.jpg",
                "http://img2.3lian.com/2014/c7/51/d/26.jpg",
                "http://img3.3lian.com/2013/c1/34/d/93.jpg",
                "http://b.zol-img.com.cn/desk/bizhi/image/3/960x600/1375841395686.jpg",
                "http://picview01.baomihua.com/photos/20120917/m_14_634834710114218750_41852580.jpg",
                "http://cdn.duitang.com/uploads/item/201311/03/20131103171224_rr2aL.jpeg",
                "http://imgrt.pconline.com.cn/images/upload/upc/tx/wallpaper/1210/17/c1/spcgroup/14468225_1350443478079_1680x1050.jpg",
                "http://pic41.nipic.com/20140518/4135003_102025858000_2.jpg",
                "http://www.1tong.com/uploads/wallpaper/landscapes/200-4-730x456.jpg",
                "http://pic.58pic.com/58pic/13/00/22/32M58PICV6U.jpg",
                "http://picview01.baomihua.com/photos/20120629/m_14_634765948339062500_11778706.jpg",
                "http://h.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=429e7b1b92ef76c6d087f32fa826d1cc/7acb0a46f21fbe09cc206a2e69600c338744ad8a.jpg",
                "http://pica.nipic.com/2007-12-21/2007122115114908_2.jpg",
                "http://cdn.duitang.com/uploads/item/201405/13/20140513212305_XcKLG.jpeg",
                "http://photo.loveyd.com/uploads/allimg/080618/1110324.jpg",
                "http://img4.duitang.com/uploads/item/201404/17/20140417105820_GuEHe.thumb.700_0.jpeg",
                "http://cdn.duitang.com/uploads/item/201204/21/20120421155228_i52eX.thumb.600_0.jpeg",
                "http://img4.duitang.com/uploads/item/201404/17/20140417105856_LTayu.thumb.700_0.jpeg",
                "http://img04.tooopen.com/images/20130723/tooopen_20530699.jpg",
                "http://www.qjis.com/uploads/allimg/120612/1131352Y2-16.jpg",
                "http://pic.dbw.cn/0/01/33/59/1335968_847719.jpg",
                "http://a.hiphotos.baidu.com/image/pic/item/a8773912b31bb051a862339c337adab44bede0c4.jpg",
                "http://h.hiphotos.baidu.com/image/pic/item/f11f3a292df5e0feeea8a30f5e6034a85edf720f.jpg",
                "http://img0.pconline.com.cn/pconline/bizi/desktop/1412/ER2.jpg",
                "http://pic.58pic.com/58pic/11/25/04/91v58PIC6Xy.jpg",
                "http://img3.3lian.com/2013/c2/32/d/101.jpg",
                "http://pic25.nipic.com/20121210/7447430_172514301000_2.jpg",
                "http://img02.tooopen.com/images/20140320/sy_57121781945.jpg",
                "http://www.renyugang.cn/emlog/content/plugins/kl_album/upload/201004/852706aad6df6cd839f1211c358f2812201004120651068641.jpg"
        };
        for(String url : imageUrls) {
            mUrlList.add(url);
        }
        int screenWidth = MyUtils.getScreenMetrics(this).widthPixels;
        int space = (int)MyUtils.dp2px(this, 20f);
        mImageWidth = (screenWidth - space) / 3;
        isWifi = MyUtils.isWifi(this);
        if(isWifi) {
            mCanGetBitmapFromNetWork = true;
        }
    }

    private void initView() {
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setOnScrollListener(this);
        mImageAdapter = new ImageAdapter(this);
        gridView.setAdapter(mImageAdapter);

        if(!isWifi){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("注意")
                    .setMessage("初次使用会从网络下载大概5MB的图片，确认要下载吗？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mCanGetBitmapFromNetWork = true;
                            mImageAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("否", null)
                    .show();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == GridView.OnScrollListener.SCROLL_STATE_IDLE) {
            isGridViewIdle = true;
            mImageAdapter.notifyDataSetChanged();
        } else {
            isGridViewIdle = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //do noting.
    }

    private class ImageAdapter extends BaseAdapter{

        private Context context;

        public ImageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mUrlList.size();
        }

        @Override
        public Object getItem(int position) {
            return mUrlList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //从布局中加载item项，保存到convertView中，并将item中需要赋值的控件保存到ViewHolder中，这样就不必每次调用findViewById来查找控件了。
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_view, parent, false);
                ViewHolder viewHodler = new ViewHolder();
                viewHodler.imageView = (ImageView) convertView.findViewById(R.id.item_image_view);
                convertView.setTag(viewHodler);
            }

            //获取item中需要赋值的控件
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            ImageView imageView = viewHolder.imageView;

            //虽然item_grid_view.xml文件中有给imageView设置默认图片，但是item_grid_view.xml是会被复用的，那么imageView的图片发生了改变，此处必须重新设置。
            imageView.setImageResource(R.drawable.image_default);

            /*
            * isGridViewIdle，表示仅当GridView停止滚动才加载图片，这样可以提高性能，有效解决滚动卡顿现象。
            *
            * mCanGetBitmapFromNetWork，表示是否允许联网下载图片，用户可能会拒绝使用流量下载图片。
            * */
            if(mCanGetBitmapFromNetWork && isGridViewIdle) {
                String url = (String) getItem(position);
                mBitmapLoader.bindBitmap(url, imageView, mImageWidth, mImageWidth);
            }
            return convertView;
        }
    }

    class ViewHolder{
        ImageView imageView;
    }
}
