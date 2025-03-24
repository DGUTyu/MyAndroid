package cn.example.performance.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * 缓存Bitmap
 */
public class ImageCache {
    private static LruCache<String, Bitmap> imageCache;

    static {
        //一般应用最大可用内存为256MB,可以在清单文件设置android:largeHeap="true"分配到 512MB 或更大
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存的1/10作为缓存
        final int cacheSize = maxMemory / 10;

        // Log 打印 cacheSize
        double cacheSizeMB = cacheSize / 1024.0;
        Log.d("ImageCache", "Cache size: " + cacheSize + " KB，即 " + String.format("%.2f", cacheSizeMB) + " MB");

        // 初始化 LruCache，设置最大缓存大小为 cacheSize
        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 计算并返回 bitmap 的大小（单位：KB）
                int sizeInKB = bitmap.getByteCount() / 1024;
                Log.d("ImageCache", "SizeOf called - Key: " + key + ", Bitmap size: " + sizeInKB + " KB");
                return sizeInKB;
            }
        };
    }

    public static void putBitmap(String key, Bitmap bitmap) {
        // 在缓存中如果没有该 bitmap，则加入缓存
        if (getBitmap(key) == null) {
            Log.d("ImageCache", "Adding new bitmap to cache - Key: " + key + ", Bitmap size: " + bitmap.getByteCount() / 1024 + " KB");
            imageCache.put(key, bitmap);
        } else {
            Log.d("ImageCache", "Bitmap already exists in cache - Key: " + key);
        }
    }

    public static Bitmap getBitmap(String key) {
        // 从缓存中获取 bitmap
        Bitmap cachedBitmap = imageCache.get(key);
        if (cachedBitmap != null) {
            Log.d("ImageCache", "Bitmap retrieved from cache - Key: " + key + ", Bitmap size: " + cachedBitmap.getByteCount() / 1024 + " KB");
        } else {
            Log.d("ImageCache", "Bitmap not found in cache - Key: " + key);
        }
        return cachedBitmap;
    }
}
