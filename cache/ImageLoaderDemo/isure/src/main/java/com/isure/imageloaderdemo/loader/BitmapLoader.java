package com.isure.imageloaderdemo.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
* BitmapLoader实现了缓存和图片压缩功能。
*
* 图片压缩，这是降低OOM概率的有效手段。
*
* 内存缓存和磁盘缓存，通过这两级缓存极大地提高了程序的效率并且有效地降低了对用户所造成的流量消耗，
* 只有当这两级缓存都不可用时才需要从网络拉取图片。
* */
public class BitmapLoader {

    private static final String TAG = "BitmapLoader";

    private static final int CPU_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), sThreadFactory);

    private static final int WHAT_BITMAP_RESULT = 0;
    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == WHAT_BITMAP_RESULT) {
                BitmapResult bitmapResult = (BitmapResult) msg.obj;
                if(bitmapResult != null) {
                    ImageView imageView = bitmapResult.imageView;
                    String imageViewUrl = (String) imageView.getTag();
                    if(bitmapResult.url.equals(imageViewUrl)) {
                        imageView.setImageBitmap(bitmapResult.bitmap);
                    } else {
                        /*
                         * 当图片加载完毕时，如果imageView被复用，会进入此分支。
                         * 此时不要给imageView设置图片，否则GridView会出现列表错位。
                         * */
                        Log.d(TAG, "bind bitmap to ImageView, but url has changed, ignored!");
                    }
                }
            }
        }
    };

    private Context mContext;

    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskCache;

    //定义磁盘缓存大小为50MB。
    private static final int DISK_CACHE_SIZE = 1024*1024*50;
    private static final int DISK_CACHE_INDEX = 0;

    private boolean mIsDiskLruCacheCreated = false;

    public BitmapLoader(Context context) {
        mContext = context;

        /*
        * 1.创建内存缓存
        *
        * 内存缓存大小为内存的 1/8
        * */
        int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        int memoryCacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(memoryCacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                /*
                * sizeOf的大小单位需要和总容量的单位一样。
                *
                * 本实例中，通过 图片的行字节数 * 图片的高度 先得出图片的大小， 再除以1024 将其单位转换为KB。
                * */
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };

        //2.创建磁盘缓存
        File diskCacheDirectory = getDiskCacheDir(mContext, "bitmap");
        if(!diskCacheDirectory.exists()){
            diskCacheDirectory.mkdir();
        }
        //如果磁盘剩余空间不足，则不会创建磁盘缓存。
        if(diskCacheDirectory.getUsableSpace() > DISK_CACHE_SIZE) {
            try {
                mDiskCache = DiskLruCache.open(diskCacheDirectory, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key){
        return mMemoryCache.get(key);
    }

    public void addBitmapToMemoryCache(String key, Bitmap value){
        if(mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, value);
        }
    }

    private String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private boolean downloadUrlToStream(String urlStr, OutputStream outputStream) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn =(HttpURLConnection) url.openConnection();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(conn.getInputStream());
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            byte[] bytes = new byte[1024];
            int len;
            while((len = bufferedInputStream.read(bytes)) != -1){
                bufferedOutputStream.write(bytes, 0, len);
            }
            bufferedInputStream.close();
            bufferedOutputStream.close();
            conn.disconnect();
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    //isure：难道磁盘缓存创建失败，运行线程就不要做检查了，内存缓存就不用了，图片就不需要压缩啦？？？
    private Bitmap downloadBitmapFromNetwork(String urlStr) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            bufferedInputStream.close();
            conn.disconnect();
        } catch (IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    private File getDiskCacheDir(Context context, String subDir) {
        File cacheDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = context.getExternalCacheDir();
        } else {
            cacheDir = context.getCacheDir();
        }
        return new File(cacheDir, subDir);
    }

    //isure：保存到磁盘缓存时，并没有使用图片压缩技术，为什么呢，难道保存一张小图片到磁盘不是更省空间？
    private void addBitmapToDiskCache(String url) throws IOException {
        if(Looper.getMainLooper().equals(Looper.myLooper())) {
            throw new RuntimeException("can not visit network form UI Thread.");
        }
        if(mDiskCache == null) {
            return;
        }

        //因为图片的url中可能有中文或特殊字符，这将影响url在Android中的直接使用，一般采用url的md5值作为key。
        String key = hashKeyFormUrl(url);
        //如果key对应的缓存对象正在被编辑，则mDiskCache.edit(key)返回null。即DiskLruCache不允许同时编辑一个缓存对象。
        DiskLruCache.Editor editor = mDiskCache.edit(key);
        if(editor != null) {
            //得到key对应的文件输出流
            // 下面注释代码会抛出ClassCastException，即不能转换为FileOutputStream。
//            FileOutputStream fileOutputStream = (FileOutputStream) editor.newOutputStream(DISK_CACHE_INDEX);
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            //downloadUrlToStream方法功能是：从url中下载图片保存到fileOutputStream中。
            if(downloadUrlToStream(url, outputStream)) {
                //经过上面的步骤，其实并没有真正地将图片写入文件系统，还必须通过Editor的commit()来提交写入操作。
                editor.commit();
            } else {
                //如果图片下载过程发生了异常，那么可以通过Editor的abort来回退整个操作。
                editor.abort();
            }
            mDiskCache.flush();
        }
    }

    //isure：加载图片和保存到内存缓存时，使用了图片压缩技术，这样从内存缓存和磁盘缓存加载的图片都是压缩后的图片。
    private Bitmap getBitmapFromDiskCache(String url, int reqWidth, int reqHeigth) throws IOException {
        if(Looper.getMainLooper().equals(Looper.myLooper())) {
            throw new RuntimeException("Load bitmap from UI Thread, this is not recommended!");
        }
        if(mDiskCache == null) {
            return null;
        }

        Bitmap bitmap = null;
        //和缓存的添加过程类似，缓存查找过程也需要将url转换为可以
        String key = hashKeyFormUrl(url);
        //如果key对应的磁盘缓存不存在，则mDiskCache.get(key)返回null。
        DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
        if(snapshot != null) {
            //得到key对应的文件输入流
            FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            /*
            * 注意：下面压缩图片的代码，最终调用的是 BitmapFactory.decodeFileDescriptor 而不是  BitmapFactory.decodeStream，
            * 即传入的是文件描述符而不是文件输入流，原因如下：。
            *
            * 因为FileInputStream是一种有序的文件流，而两次decodeStream调用影响了文件流的位置属性，导致了第二次decodeStream时
            * 得到的是null。
            * */
            FileDescriptor fd = fileInputStream.getFD();
            /*
            * 为了避免加载图片过程中导致的OOM问题，一般不建议直接加载原始图片。
            *
            * 下面BitmapResizer类的decodeSampledBitmapFromFileDescriptor方法返回的就是压缩（即缩小）后的图片。
            * */
            bitmap = new BitmapResizer().decodeSampledBitmapFromFileDescriptor(fd, reqWidth, reqHeigth);
            if(bitmap != null) {
                //将图片加入内存缓存
                addBitmapToMemoryCache(key, bitmap);
            }
        }
        return bitmap;
    }

    public Bitmap getBitmapFromNetwork(String url, int reqWidh, int reqHeight) throws IOException {
        addBitmapToDiskCache(url);
        return getBitmapFromDiskCache(url, reqWidh, reqHeight);
    }

    /*
    * 同步加载
    *
    * 同步加载是指，loadBitmap方法与调用loadBitmap方法的代码运行在同一个线程，因此用户必须在工作线程中调用loadBitmap方法。
    * */
    public Bitmap loadBitmap(String url, int reqWidh, int reqHeight) {
        String key = hashKeyFormUrl(url);

        //1.首先从内存缓存中加载图片
        Bitmap bitmap = getBitmapFromMemoryCache(key);
        if(bitmap != null) {
            Log.d(TAG, "getBitmapFromMemoryCache url = " + url);
            return bitmap;
        }

        if(mIsDiskLruCacheCreated) {
            try {
                //2.如果内存缓存中没有这个图片，再从磁盘缓存中加载图片。并将图片加到内存缓存中。
                bitmap = getBitmapFromDiskCache(url, reqWidh, reqHeight);
                if(bitmap != null) {
                    Log.d(TAG, "getBitmapFromDiskCache url = " + url);
                    return bitmap;
                }

                //3.如果内存缓存和磁盘缓存中都没有这个图片，则从网络下载图片。并将图片加到内存缓存和磁盘缓存中。
                bitmap = getBitmapFromNetwork(url, reqWidh, reqHeight);
                if(bitmap != null) {
                    Log.d(TAG, "getBitmapFromNetwork url = " + url);
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //4.如果磁盘缓存创建失败，则从网络下载图片。图片不会被缓存。
            bitmap = downloadBitmapFromNetwork(url);
            if (bitmap != null) {
                Log.d(TAG, "disk cache create failed, downloadBitmapFromNetwork url = " + url);
                return bitmap;
            }
        }

        Log.d(TAG, "load bitmap failed url = " + url);
        return null;
    }

    /*
     * 异步加载
     *
     * 异步加载是指，bindBitmap方法内部自己在工作线程中加载图片并将图片设置给所需的ImageView，这样就保证了bindBitmap方法是运行在工作线程中。
     * */
    public void bindBitmap(final String url, final ImageView imageView, final int reqWidh, final int reqHeight) {
        imageView.setTag(url);

        String key = hashKeyFormUrl(url);

        //1.首先从内存缓存中加载图片
        final Bitmap bitmap = getBitmapFromMemoryCache(key);
        if(bitmap != null) {
            Log.d(TAG, "getBitmapFromMemoryCache url = " + url);
            imageView.setImageBitmap(bitmap);
            return;
        }

        /*
         * 此处采用线程池和handler，来加载并绑定图片。没有采用普通线程或AsyncTask等方式，原因如下：
         *
         * 1.普通线程：如果直接采用普通的线程去加载图片，随着列表的滑动这可能会产生大量的线程，这并不利于整体效率的提升。
         *
         * 2.AsyncTask：虽然封装了线程池和Handler，但是AsyncTask无法实现并发的效果，这显然是不能接受的。
         * */
        Runnable loadBimapRunnable = new Runnable() {
            @Override
            public void run() {
                Bitmap diskCacheBitmap = loadBitmap(url, reqWidh, reqHeight);
                if(diskCacheBitmap != null) {
                    BitmapResult result = new BitmapResult(imageView, diskCacheBitmap, url);
                    Message message = mMainHandler.obtainMessage(WHAT_BITMAP_RESULT, result);
                    mMainHandler.sendMessage(message);
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBimapRunnable);
    }

    class BitmapResult {
        ImageView imageView;
        Bitmap bitmap;
        String url;

        public BitmapResult(ImageView imageView, Bitmap bitmap, String url){
            this.imageView = imageView;
            this.bitmap = bitmap;
            this.url = url;
        }
    }

}
