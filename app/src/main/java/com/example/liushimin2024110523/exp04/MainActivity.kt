package com.example.liushimin2024110523.exp04

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp04.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 当前玩家信息（内存中保存，无需持久化到文件）
    private var playerName: String = ""
    private var playerGender: String = ""
    private var playerPhone: String = ""
    private var highScore: Int = 0

    // 信息修改页面回传结果
    private val editInfoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            playerName = data.getStringExtra(getString(R.string.extra_name)) ?: playerName
            playerGender = data.getStringExtra(getString(R.string.extra_gender)) ?: playerGender
            playerPhone = data.getStringExtra(getString(R.string.extra_phone)) ?: playerPhone
            refreshPlayerInfo()
            Toast.makeText(this, R.string.info_saved, Toast.LENGTH_SHORT).show()
        }
    }

    // 翻牌游戏页面回传最高分
    private val gameLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val score = data.getIntExtra(getString(R.string.extra_high_score), 0)
            if (score > highScore) {
                highScore = score
                refreshPlayerInfo()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化默认玩家信息
        playerName = getString(R.string.default_name)
        playerGender = getString(R.string.default_gender)
        playerPhone = getString(R.string.default_phone)
        highScore = getString(R.string.default_high_score).toIntOrNull() ?: 0

        refreshPlayerInfo()

        // 修改个人信息按钮
        binding.btnEditInfo.setOnClickListener {
            val intent = Intent(this, EditInfoActivity::class.java).apply {
                putExtra(getString(R.string.extra_name), playerName)
                putExtra(getString(R.string.extra_gender), playerGender)
                putExtra(getString(R.string.extra_phone), playerPhone)
            }
            editInfoLauncher.launch(intent)
        }

        // 开始翻牌游戏按钮
        binding.btnPlayGame.setOnClickListener {
            val intent = Intent(this, MemoryGameActivity::class.java)
            gameLauncher.launch(intent)
        }
    }

    /** 刷新界面上的玩家信息显示 */
    private fun refreshPlayerInfo() {
        binding.tvName.text = getString(R.string.label_name) + playerName
        binding.tvGender.text = getString(R.string.label_gender) + playerGender
        binding.tvPhone.text = getString(R.string.label_phone) + playerPhone
        binding.tvHighScore.text = getString(R.string.label_high_score) +
                highScore + getString(R.string.score_unit)
    }
}
