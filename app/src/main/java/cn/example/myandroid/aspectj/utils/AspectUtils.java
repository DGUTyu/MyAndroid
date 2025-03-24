package cn.example.myandroid.aspectj.utils;


import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import cn.example.myandroid.aspectj.annotate.CheckPermission;
import cn.example.myandroid.aspectj.annotate.TrackEvent;

/**
 * aspectjx 是一个用于 Android 项目的 AOP（面向切面编程）插件，基于 AspectJ 实现。它的常用功能包括：
 * <p>
 * 方法执行监控（统计方法执行时间）
 * 日志记录（自动打印方法调用信息）
 * 权限校验（检查是否有权限执行方法）
 * 防止重复点击（限制按钮短时间内多次点击）
 * 自动埋点（无侵入式添加埋点逻辑）
 */

@Aspect
public class AspectUtils {
    //1. 统计方法执行时间
    @Around("execution(* cn.example.myandroid..*(..))")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        Log.d("TimeAspect", joinPoint.getSignature() + " 耗时：" + (end - start) + "ms");
        return result;
    }

    //2. 防止按钮短时间内多次点击
    // 然后在方法上添加注解：@NoDoubleClick
    // 2秒内防止多次点击
    private static final long MIN_CLICK_INTERVAL = 1000;
    private static long lastClickTime = 0;

    @Pointcut("execution(@cn.example.myandroid.aspect.NoDoubleClick * *(..))")
    public void noDoubleClickMethod() {}

    @Around("noDoubleClickMethod()")
    public void checkDoubleClick(ProceedingJoinPoint joinPoint) throws Throwable {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_INTERVAL) {
            lastClickTime = currentTime;
            // 允许点击
            joinPoint.proceed();
        } else {
            Log.d("ClickAspect", "短时间内重复点击，拦截");
        }
    }

    //3. 权限校验
    //让方法声明自己需要的权限，例如 @CheckPermission(Manifest.permission.CAMERA)
    @Pointcut("execution(@cn.example.myandroid.aspect.CheckPermission * *(..))")
    public void checkPermissionMethod() {}

    @Around("checkPermissionMethod()")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CheckPermission annotation = signature.getMethod().getAnnotation(CheckPermission.class);
        if (annotation != null) {
            String permission = annotation.value();
            Log.d("PermissionAspect", "权限检查: " + permission);
            Context context = getContext(joinPoint);
            if (context != null && ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionAspect", "权限不足：" + permission);
                // 触发权限申请（可使用 EventBus、回调等方式通知 UI 申请权限）
                return null;
            }
        }
        // 权限允许，则继续执行方法
        return joinPoint.proceed();
    }

    private Context getContext(ProceedingJoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        if (target instanceof Context) {
            return (Context) target;
        } else if (target instanceof android.view.View) {
            return ((android.view.View) target).getContext();
        }
        return null;
    }

    //4.自动打印方法调用信息
    //当 cn.example.myandroid 包下的任意方法被调用时，都会自动打印日志：
    @Pointcut("execution(* cn.example.myandroid..*(..))")
    public void logMethods() {}

    @Around("logMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toString();
        Log.d("LogAspect", "方法开始: " + methodName);

        Object result = joinPoint.proceed();

        Log.d("LogAspect", "方法结束: " + methodName);
        return result;
    }

    //5.自动埋点
    //点击事件添加 @TrackEvent("描述，对应annotation.value")
    @Pointcut("execution(@cn.example.myandroid.aspect.TrackEvent * *(..))")
    public void trackEventMethod() {}

    @Around("trackEventMethod()")
    public Object trackEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        TrackEvent annotation = signature.getMethod().getAnnotation(TrackEvent.class);
        if (annotation != null) {
            Log.d("TrackAspect", "埋点事件: " + annotation.value());
            // 可以在这里调用埋点 SDK，比如 MobclickAgent.onEvent(...)
        }
        // 继续执行方法
        return joinPoint.proceed();
    }

    @Around("execution(* androidx.appcompat.app.AppCompatActivity.setContentView(..))")
    public void getSetContentViewTime(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String name = signature.toShortString();
        long time = System.currentTimeMillis();
        try {
            joinPoint.proceed(); // 执行方法
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        Log.d("ActivityAspect", name + " cost " + (System.currentTimeMillis() - time));
    }

}
