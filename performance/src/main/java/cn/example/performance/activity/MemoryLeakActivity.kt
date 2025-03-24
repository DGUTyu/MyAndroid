package cn.example.performance.activity

import android.annotation.TargetApi
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import cn.example.performance.CallBack
import cn.example.performance.CallBackManager
import cn.example.performance.R

class MemoryLeakActivity : AppCompatActivity(), CallBack {
    // 起始帧时间戳，用于计算每帧的时间间隔
    private var mStartFrameTime: Long = 0

    // 记录已绘制的帧数
    private var mFrameCount = 0

    // FPS监控的时间间隔，单位为毫秒。这里设置为 160 毫秒，即每 160 毫秒计算一次帧率
    private val MONITOR_INTERVAL = 160L

    // 将时间间隔从毫秒转换为纳秒（因为 Choreographer 使用的是纳秒单位）
    private val MONITOR_INTERVAL_NANOS = MONITOR_INTERVAL * 1000L * 1000L

    // FPS 计算时的最大时间间隔，这里设置为 1000 毫秒（即 1 秒）
    // 计算 FPS 的单位时间间隔为 1000ms，即 FPS/s;
    private val MAX_INTERVAL = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance_memoryleak)
        val imageView = findViewById<ImageView>(R.id.iv_memoryleak)
        val imageViewOther = findViewById<ImageView>(R.id.iv_memoryleak_other)
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.splash)
        imageView.setImageBitmap(bitmap)
        imageViewOther.setImageBitmap(bitmap)

        CallBackManager.addCallBack(this)
        //getFPS()
    }

    override fun onDestroy() {
        super.onDestroy()
        CallBackManager.removeCallBack(this)
    }

    override fun dpOperate() {
        // do sth
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun getFPS() {
        // 如果系统版本低于 Jelly Bean，则不执行帧率计算
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return
        }

        // 使用 Choreographer 来在每一帧绘制时执行回调
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                // 第一次执行时，记录起始时间戳
                if (mStartFrameTime == 0L) {
                    mStartFrameTime = frameTimeNanos
                }

                // 计算当前帧与上一帧的时间间隔
                val interval = frameTimeNanos - mStartFrameTime

                // 如果时间间隔超过设定阈值，则进行帧率计算
                if (interval > MONITOR_INTERVAL_NANOS) {
                    // 根据帧数和时间间隔计算 FPS，单位是帧每秒
                    val fps = ((mFrameCount * 1000L * 1000L).toDouble() / interval) * MAX_INTERVAL

                    // 打印 FPS 和帧计数信息
                    Log.d("FPSMonitor", "FPS: $fps, Frame count: $mFrameCount, Interval: $interval")

                    // 重置帧计数器和起始时间戳
                    mFrameCount = 0
                    mStartFrameTime = 0
                } else {
                    // 如果时间间隔没有超过阈值，继续增加帧计数
                    ++mFrameCount
                }

                // 继续请求下一帧的回调
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }


}