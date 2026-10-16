package com.example.liushimin2024110523.exp03

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp03.databinding.Activity2Binding

/**
 * Activity2：用户输入内容后点"确定"，把内容返回给 MainActivity 并关闭自己。
 */
class Activity2 : AppCompatActivity() {

    private lateinit var binding: Activity2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOk.setOnClickListener {
            // 打包回程数据：无参 Intent + putExtra
            val data = Intent().putExtra(
                getString(R.string.back_key_result),
                binding.etInput.text.toString()
            )
            // 回传并关闭自己（不 finish 结果永远不会回来）
            setResult(RESULT_OK, data)
            finish()
        }
    }
}
