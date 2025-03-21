package cn.example.myandroid.tasks;

import android.graphics.Bitmap;
import android.widget.ImageView;

import cn.example.performance.utils.ImageHook;
import cn.example.task.launchstarter.task.Task;
import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;

public class HookInitTask extends Task {
    private String mDeviceId;

    @Override
    public void run() {
        mDeviceId = "123";
        DexposedBridge.hookAllConstructors(ImageView.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                DexposedBridge.findAndHookMethod(ImageView.class, "setImageBitmap", Bitmap.class, new ImageHook());
            }
        });
        /**
         *        DexposedBridge.hookAllConstructors(ImageView::class.java, object : XC_MethodHook() {
         *             @Throws(Throwable::class)
         *             override fun afterHookedMethod(param: MethodHookParam) {
         *                 super.afterHookedMethod(param)
         *                 DexposedBridge.findAndHookMethod(ImageView::class.java, "setImageBitmap", Bitmap::class.java, ImageHook())
         *             }
         *         })
         */
    }
}
