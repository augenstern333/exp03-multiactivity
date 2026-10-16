package com.example.liushimin2024110523.exp03

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp03.databinding.Activity1Binding

/**
 * Activity1：接收 MainActivity 正向传过来的参数并显示。
 */
class Activity1 : AppCompatActivity() {

    private lateinit var binding: Activity1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 拆信：intent 是"启动我这个 Activity 的那个 Intent"
        val name = intent.getStringExtra(getString(R.string.extra_key_name))
        // getStringExtra 返回 String?，可能为 null，用 ?: 兜底（兜底文本也走资源）
        binding.tvReceived.text = name ?: getString(R.string.no_param)
    }
}
