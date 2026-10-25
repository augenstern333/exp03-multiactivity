package com.example.liushimin2024110523.exp04

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp04.databinding.ActivityEditInfoBinding

class EditInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 接收从 MainActivity 传来的当前信息，填入输入框
        val name = intent.getStringExtra(getString(R.string.extra_name)) ?: ""
        val gender = intent.getStringExtra(getString(R.string.extra_gender)) ?: ""
        val phone = intent.getStringExtra(getString(R.string.extra_phone)) ?: ""

        binding.etName.setText(name)
        binding.etGender.setText(gender)
        binding.etPhone.setText(phone)

        // 保存按钮：把修改后的信息返回给 MainActivity
        binding.btnSave.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(getString(R.string.extra_name), binding.etName.text.toString().trim())
                putExtra(getString(R.string.extra_gender), binding.etGender.text.toString().trim())
                putExtra(getString(R.string.extra_phone), binding.etPhone.text.toString().trim())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // 取消按钮：不保存直接返回
        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
