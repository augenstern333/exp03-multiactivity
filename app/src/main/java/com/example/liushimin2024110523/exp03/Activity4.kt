package com.example.liushimin2024110523.exp03

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp03.databinding.Activity4Binding

/**
 * Activity4：StopWatch 秒表。
 * 关键点：屏幕旋转 = Activity 销毁重建，必须用 onSaveInstanceState
 * 保存已走毫秒数，重建后恢复，保证横竖屏切换计时连续。
 */
class Activity4 : AppCompatActivity() {

    private lateinit var binding: Activity4Binding

    private var elapsed: Long = 0L      // 已累计毫秒数（暂停时定格）
    private var baseTime: Long = 0L     // 计时起点（系统时钟时间戳）
    private var isRunning = false       // 是否正在计时

    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            updateDisplay()
            handler.postDelayed(this, 100)   // 100ms 刷一次，秒表更顺滑
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // ★ 恢复：旋转后重建，从 Bundle 拿回之前存的状态
        savedInstanceState?.let {
            elapsed = it.getLong("elapsed")
            isRunning = it.getBoolean("is_running")
            baseTime = it.getLong("base_time")
        }
        // 如果恢复时正在计时，立刻接着跑
        if (isRunning) startTicking()

        binding.btnStart.setOnClickListener {
            if (!isRunning) {
                // 从"已累计的 elapsed"接着计，而不是从 0 开始
                baseTime = SystemClock.elapsedRealtime() - elapsed
                isRunning = true
                startTicking()
            }
        }

        binding.btnPause.setOnClickListener {
            if (isRunning) {
                // 暂停：定格当前已走毫秒数
                elapsed = SystemClock.elapsedRealtime() - baseTime
                isRunning = false
                stopTicking()
                updateDisplay()
            }
        }

        binding.btnReset.setOnClickListener {
            elapsed = 0L
            isRunning = false
            stopTicking()
            updateDisplay()
        }
    }

    // ★ 保存：销毁前被系统自动调用（旋转、切后台被回收等）
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 先把"当前已走毫秒数"算出来存进去
        if (isRunning) {
            elapsed = SystemClock.elapsedRealtime() - baseTime
        }
        outState.putLong("elapsed", elapsed)
        outState.putBoolean("is_running", isRunning)
        outState.putLong("base_time", baseTime)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTicking()   // 页面销毁时移除回调，防止 Handler 泄漏
    }

    private fun startTicking() = handler.post(tick)

    private fun stopTicking() = handler.removeCallbacks(tick)

    private fun updateDisplay() {
        val showMs = if (isRunning) SystemClock.elapsedRealtime() - baseTime else elapsed
        binding.tvTime.text = formatTime(showMs)
    }

    // 毫秒 → 分:秒:厘秒，格式串在 strings.xml（time_format）
    private fun formatTime(ms: Long): String {
        val minutes = ms / 60000
        val seconds = (ms % 60000) / 1000
        val centis = (ms % 1000) / 10
        return getString(R.string.time_format, minutes, seconds, centis)
    }
}
