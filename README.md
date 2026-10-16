# exp03-multiactivity

Android 移动应用开发 实验三：多 Activity。

## 功能

- **① 正向传参**：MainActivity 点第 1 个按钮启动 Activity1，`putExtra` 带参数过去，Activity1 用 `getStringExtra` 读出并显示。
- **② 返回结果**：MainActivity 点第 2 个按钮启动 Activity2，Activity2 输入内容点“确定”，`setResult` + `finish()` 把内容返回 MainActivity 显示。
- **③ 隐式拍照**：MainActivity 点第 3 个按钮启动 Activity3，Activity3 用隐式 Intent（`MediaStore.ACTION_IMAGE_CAPTURE`）调起系统相机拍照，拍完在 ImageView 回显缩略图（参考老师附件 PDF，新 API 实现）。
- **④ StopWatch**：MainActivity 点第 4 个按钮启动 Activity4，实现开始/暂停/重置秒表；用 `onSaveInstanceState` 保存状态，横屏竖屏切换时计时连续不丢。
- **ViewBinding**：`build.gradle.kts` 已开启 `viewBinding = true`，界面操作全部走自动生成的 Binding 类。
- **无硬编码字符串**：所有文本（含 Intent 的 key、时间格式串）在 `res/values/strings.xml`，布局用 `@string/`，代码用 `getString(...)`。
- **APP 标题**：`刘胜敏2024110523`（姓名+学号）。

## 目录结构

```
app/src/main/java/com/example/liushimin2024110523/exp03/
├── MainActivity.kt   # 主界面：4 个按钮 + 接收 Activity2 返回结果
├── Activity1.kt      # 正向传参：接收参数并显示
├── Activity2.kt      # 返回结果：setResult 回传
├── Activity3.kt      # 隐式拍照：ACTION_IMAGE_CAPTURE
└── Activity4.kt      # StopWatch：onSaveInstanceState 保存状态
app/src/main/res/layout/
├── activity_main.xml
├── activity1.xml
├── activity2.xml
├── activity3.xml
└── activity4.xml
```

## 环境

- Android Studio 2026.x
- AGP 9.3.0 / Gradle 9.5.0
- minSdk 29 / targetSdk 37 / compileSdk 37
- Kotlin

## 运行

用 Android Studio 打开本目录，连接 Android 真机（开启 USB 调试）或模拟器，点击 Run。

命令行编译：`gradlew.bat assembleDebug`，APK 输出在 `app/build/outputs/apk/debug/`。

## 提交（gitee）

1. `git init` 并推送到你在 gitee 上的项目仓库；
2. 实验报告末尾附上 gitee 地址。
