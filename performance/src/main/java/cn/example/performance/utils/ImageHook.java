package cn.example.performance.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import cn.example.performance.R;
import de.robv.android.xposed.XC_MethodHook;

/**
 * ImageHook 监测 `ImageView` 绑定的 `Bitmap` 是否过大，避免高内存占用问题。
 * 适用于 Xposed / Dexposed Hook 环境。
 */
public class ImageHook extends XC_MethodHook {
    // 定义一个常量来调整比例
    private static final float RATIO = 1.5f;

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        ImageView view = (ImageView) param.thisObject;
        if (view.getTag(R.id.view_pre_draw_hook) != null) {
            return;
        }
        // setTag 避免重复优化
        view.setTag(R.id.view_pre_draw_hook, true);

        Bitmap bitmap = (Bitmap) param.args[0];
        if (bitmap == null) {
            return;
        }
        // 1.监控 setImageBitmap，日志分析大图
        final int bw = bitmap.getWidth();
        final int bh = bitmap.getHeight();
        //获取 ImageView 的 hashCode，作为日志标识
        String viewIdentifier = "ImageView#" + view.hashCode();
        if (bw > 1024 || bh > 1024) {
            Log.w("ImageHook", "大图警告: " + viewIdentifier + " Bitmap(" + bw + ", " + bh + ") 可能导致内存占用过高");
        }
        // 2.拦截 setImageBitmap，自动缩放大图
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int w = view.getWidth();
                int h = view.getHeight();
                if (w > 0 && h > 0) {
                    // 在图片的宽高超过 ImageView 宽高的 1.5 倍时才进行优化操作
                    if (bw >= (w * RATIO) && bh >= (h * RATIO)) {
                        // 3.LruCache + ImageViewHook 进行图片缓存
                        // 生成唯一的缓存 key，结合 bitmap.hashCode() 避免不同图片但相同尺寸被覆盖
                        String cacheKey = bitmap.hashCode() + "_" + bw + "x" + bh;
                        Bitmap cachedBitmap = ImageCache.getBitmap(cacheKey);

                        if (cachedBitmap == null || cachedBitmap.isRecycled()) {
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
                            ImageCache.putBitmap(cacheKey, scaledBitmap);
                            view.setImageBitmap(scaledBitmap);
                            Log.d("ImageHook", "Bitmap real size: (" + bw + "," + bh + ")");
                            Log.d("ImageHook", "Bitmap resized onPreDraw: (" + w + "," + h + ")");
                            Log.d("ImageHook", "Original Bitmap size: " + bitmap.getByteCount() + " bytes");
                            Log.d("ImageHook", "Resized Bitmap size: " + scaledBitmap.getByteCount() + " bytes");
                        } else {
                            view.setImageBitmap(cachedBitmap);
                            Log.d("ImageHook", "Use cachedBitmap");
                        }
                    }
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    view.setTag(R.id.view_pre_draw_hook, null);
                }
                return true;
            }
        });

    }

//    @Override
//    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        super.afterHookedMethod(param);
//        // 实现我们的逻辑:获取 ImageView 实例，并检查其绑定的 Drawable
//        ImageView imageView = (ImageView) param.thisObject;
//        checkBitmap(imageView, ((ImageView) param.thisObject).getDrawable());
//    }

    /**
     * 检查 `Bitmap` 是否超过 `View` 期望尺寸的 2 倍
     */
    private static void checkBitmap(Object thiz, Drawable drawable) {
        if (drawable instanceof BitmapDrawable && thiz instanceof View) {
            final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                final View view = (View) thiz;
                int width = view.getWidth();
                int height = view.getHeight();
                if (width > 0 && height > 0) {
                    // 图标宽高都大于view带下的2倍以上，则警告，同时进行缩放
                    if (bitmap.getWidth() >= (width << 1)
                            && bitmap.getHeight() >= (height << 1)) {
                        warn(bitmap.getWidth(), bitmap.getHeight(), width, height, new RuntimeException("Bitmap size too large"));
                        //filter = true 开启平滑插值算法（双线性过滤）
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        ((ImageView) view).setImageBitmap(scaledBitmap);
                        Log.d("ImageHook", "Bitmap resized to fit ImageView: (" + width + "," + height + ")");
                        Log.d("ImageHook", "Original Bitmap size: " + bitmap.getByteCount() + " bytes");
                        Log.d("ImageHook", "Resized Bitmap size: " + scaledBitmap.getByteCount() + " bytes");
                    }
                } else {
                    if (view.getTag(R.id.view_pre_draw_hook) != null) {
                        // 避免重复注册
                        return;
                    }
                    //在 View 上存储一个键值对，这里使用true是因为true作为布尔值，简单且易读，表示“已注册”
                    view.setTag(R.id.view_pre_draw_hook, true);

                    // 视图未测量，监听 `onPreDraw()` 进行检测
                    // 提前创建 RuntimeException，并在 onPreDraw() 里 获取完整的调用栈。
                    final Throwable stackTrace = new RuntimeException();
                    view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            int w = view.getWidth();
                            int h = view.getHeight();
                            if (w > 0 && h > 0) {
                                if (bitmap.getWidth() >= (w << 1)
                                        && bitmap.getHeight() >= (h << 1)) {
                                    warn(bitmap.getWidth(), bitmap.getHeight(), w, h, stackTrace);
                                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
                                    ((ImageView) view).setImageBitmap(scaledBitmap);
                                    Log.d("ImageHook", "Bitmap resized onPreDraw: (" + w + "," + h + ")");
                                    Log.d("ImageHook", "Original Bitmap size: " + bitmap.getByteCount() + " bytes");
                                    Log.d("ImageHook", "Resized Bitmap size: " + scaledBitmap.getByteCount() + " bytes");
                                }
                                view.getViewTreeObserver().removeOnPreDrawListener(this);
                                // 释放标记。setTag(int key, Object tag) 允许 tag 为 null，表示删除该 key 关联的值，相当于完全移除标记
                                // 如果用 false，getTag(R.id.view_pre_draw_hook) 仍然会返回 false，而不是 null，这意味着该 View 仍然持有一个值，但实际逻辑上它应该被视为未注册
                                view.setTag(R.id.view_pre_draw_hook, null);
                            }
                            return true;
                        }
                    });
                }
            }
        }
    }

    /**
     * 记录 `Bitmap` 过大的警告信息，不抛出异常，避免影响应用运行。
     */
    private static void warn(int bitmapWidth, int bitmapHeight, int viewWidth, int viewHeight, Throwable t) {
        String warnInfo = new StringBuilder("Bitmap size too large: ")
                .append("\n real size: (").append(bitmapWidth).append(',').append(bitmapHeight).append(')')
                .append("\n desired size: (").append(viewWidth).append(',').append(viewHeight).append(')')
                .append("\n call stack trace: \n").append(Log.getStackTraceString(t)).append('\n')
                .toString();

        Log.d("ImageHook", warnInfo);
    }

}
