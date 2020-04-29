本项目来自《Android开发艺术探索》(第12章 Bitmap的加载和Cache)

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