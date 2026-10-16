package com.example.liushimin2024110523.exp03

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp03.databinding.ActivityMainBinding

/**
 * 主界面：4 个按钮分别启动 4 个二级 Activity。
 * 同时负责接收 Activity2 返回的内容并显示（返回结果）。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // ★ Activity2 的"收件箱"：Activity 属性，只创建一次；回调里写拿到结果怎么处理
    private val activity2Launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val backText =
                    result.data?.getStringExtra(getString(R.string.back_key_result))
                binding.tvResult.text = backText ?: getString(R.string.no_param)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ① 正向传参 → Activity1
        binding.btnOpenActivity1.setOnClickListener {
            val intent = Intent(this, Activity1::class.java)
            // key 和 value 都走字符串资源，不硬编码
            intent.putExtra(getString(R.string.extra_key_name), getString(R.string.send_name))
            startActivity(intent)
        }

        // ② 返回结果 → Activity2
        binding.btnOpenActivity2.setOnClickListener {
            activity2Launcher.launch(Intent(this, Activity2::class.java))
        }

        // ③ 隐式拍照 → Activity3
        binding.btnOpenActivity3.setOnClickListener {
            startActivity(Intent(this, Activity3::class.java))
        }

        // ④ StopWatch → Activity4
        binding.btnOpenActivity4.setOnClickListener {
            startActivity(Intent(this, Activity4::class.java))
        }
    }
}
