package cn.example.base.utils;


import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

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
public class AspectUtilsTest {
    //1. 统计方法执行时间
    @Around("execution(* cn.example.base..*(..))")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        Log.d("TimeAspect", joinPoint.getSignature() + " 耗时：" + (end - start) + "ms");
        return result;
    }
}
