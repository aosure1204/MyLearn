package com.isure.imageloaderdemo.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/*
* BitmapResizer提供了图片压缩功能。
*
* BitmapFactory类，是系统提供的图片压缩功能类。
*
* BitmapFactory类提供了五类方法：decodeFile、decodeFileDescriptor、decodeResource、decodeStream、decodeByteArray，
* 分别用于支持从文件系统、资源、输入流以及字节数组中加载出一个Bitmap对象。
* */
public class BitmapResizer {

    /*
    * 通过BitmapFactory.Options就可以按一定的采样率来加载缩小后的图片，将缩小后的图片在ImageView中显示，
    * 这样就会降低内存占用从而在一定程度上避免OOM，提高了Bitmap加载时的性能。
    *
    * 通过BitmapFactory.Options来缩放图片，主要是用到了它的inSampleSize参数，即采用率。
    *
    * 官方文档指出，inSampleSize的取值应该总是为2的指数。
    * */
    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 1.先获取图片的原始宽高信息，它们对应于options的outWidth和outHeight。
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // 2.计算采样缩放率
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 3.获取压缩后的图片
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    /*
    * 计算采样缩放率
    *
    * 官方文档指出，inSampleSize的取值应该总是为2的指数。
    * */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        int inSampleSize = 2;

        while ((originalWidth / inSampleSize) >= reqWidth && (originalHeight / inSampleSize) >= reqHeight) {
            inSampleSize *= 2;
        }

        return inSampleSize / 2;
    }
}
