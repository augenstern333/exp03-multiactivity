package com.example.liushimin2024110523.exp03

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.liushimin2024110523.exp03.databinding.Activity3Binding
import java.io.File
import java.io.IOException

/**
 * Activity3：隐式 Intent 调起系统相机拍照。
 *
 * 为什么不用老师 PDF 的 data 缩略图方式？
 * 缩略图（extras.get("data")）分辨率极低、显示很小，且部分手机不处理拍摄方向（比例会歪）。
 * 这里改用【原图方案】：先用 FileProvider 把 cache/photos 下的临时文件授权给相机，
 * 相机把全分辨率照片写入文件（EXTRA_OUTPUT），回来后再采样解码显示——又大又清晰，方向也正确。
 */
class Activity3 : AppCompatActivity() {

    private lateinit var binding: Activity3Binding

    // 相机把原图写入的临时文件路径（拍完回调后读它）
    private lateinit var currentPhotoPath: String

    // 收件箱：等相机 App 拍完回来
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // 从文件读原图；采样压缩到合适尺寸，防止大图直接解码内存爆掉
                val bitmap = decodeSampledBitmap(currentPhotoPath, 1600, 1600)
                binding.ivPhoto.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 如果 Activity 因旋转/内存被重建，恢复之前拍到的照片
        currentPhotoPath = savedInstanceState?.getString(getString(R.string.photo_path_key)) ?: ""
        if (currentPhotoPath.isNotEmpty()) {
            val bitmap = decodeSampledBitmap(currentPhotoPath, 1600, 1600)
            binding.ivPhoto.setImageBitmap(bitmap)
        }

        binding.btnTakePhoto.setOnClickListener {
            // ① 隐式 Intent：只说"要拍照"，由系统匹配能处理的相机 App
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // ② 安全检查：没有能处理拍照的 App 就不启动（防止崩溃）
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                // ③ 建一个临时照片文件，并授权给相机写入（原图方案的关键）
                val photoFile = createImageFile()
                if (photoFile != null) {
                    val photoUri: Uri = FileProvider.getUriForFile(
                        this,
                        "$packageName.fileprovider",   // 与 AndroidManifest 里 provider 的 authorities 一致
                        photoFile
                    )
                    // ④ 告诉相机"拍完写到这个文件"，而不是返回缩略图
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    // ⑤ 出发，等拍完回来
                    cameraLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    // 旋转等销毁重建时，记住照片文件路径，回来还能继续显示
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::currentPhotoPath.isInitialized) {
            outState.putString(getString(R.string.photo_path_key), currentPhotoPath)
        }
    }

    // 在 cache/photos/ 下建一个临时 jpg 文件，路径记到 currentPhotoPath
    private fun createImageFile(): File? {
        return try {
            val dir = File(cacheDir, "photos").apply { mkdirs() }
            File.createTempFile("photo_", ".jpg", dir)
                .also { currentPhotoPath = it.absolutePath }
        } catch (e: IOException) {
            null
        }
    }

    /**
     * 采样解码：先把图片尺寸读出来（不占内存），算一个 inSampleSize，
     * 再按 2 的倍数缩小后解码——避免 4000x3000 的原图直接解码把内存打爆。
     */
    private fun decodeSampledBitmap(path: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        // 第一遍：只读宽高（inJustDecodeBounds = true 不解码像素）
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)

        // 算缩放倍数：原图尺寸超过目标 2 倍就继续翻倍缩小
        var sampleSize = 1
        while (bounds.outWidth / sampleSize > reqWidth * 2 ||
            bounds.outHeight / sampleSize > reqHeight * 2
        ) {
            sampleSize *= 2
        }

        // 第二遍：按 inSampleSize 真正解码
        return BitmapFactory.decodeFile(
            path,
            BitmapFactory.Options().apply { inSampleSize = sampleSize }
        )
    }
}
