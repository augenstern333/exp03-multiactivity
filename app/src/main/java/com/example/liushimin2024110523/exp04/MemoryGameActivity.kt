package com.example.liushimin2024110523.exp04

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.liushimin2024110523.exp04.databinding.ActivityMemoryGameBinding
import kotlin.random.Random

/**
 * 翻牌游戏 Activity —— MVC 模式
 *
 * Model: MemoryCard / GameModel（数据与游戏规则）
 * View:  GridLayout 中的 Button 卡片 + 状态栏
 * Controller: MemoryGameActivity（接收点击、更新 Model、同步 View）
 */
class MemoryGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoryGameBinding

    // ============ Model 层 ============

    /** 单张牌的数据模型 */
    data class MemoryCard(
        val pairId: Int,      // 配对编号：相同 pairId 的两张是一对
        val faceText: String, // 牌面图案（emoji）
        var isFlipped: Boolean = false,   // 是否翻开
        var isMatched: Boolean = false    // 是否已配对成功
    )

    /** 游戏模型：管理全部卡片与游戏状态 */
    inner class GameModel {
        val cards = mutableListOf<MemoryCard>()
        var moves: Int = 0           // 步数（每翻两张算一步）
        var matchedPairs: Int = 0    // 已配对对数
        var firstFlippedIndex: Int? = null  // 第一张翻开的牌索引

        // 6 种牌面图案（emoji），每种出现 2 次
        private val emojis = listOf("🐶", "🐱", "🐰", "🦊", "🐼", "🐨")

        /** 初始化一局新游戏：洗牌 + 重置状态 */
        fun newGame() {
            cards.clear()
            moves = 0
            matchedPairs = 0
            firstFlippedIndex = null

            // 生成 6 对牌，然后洗牌
            emojis.forEachIndexed { index, emoji ->
                cards.add(MemoryCard(pairId = index, faceText = emoji))
                cards.add(MemoryCard(pairId = index, faceText = emoji))
            }
            cards.shuffle()
        }

        /**
         * 点击某张牌，返回当前操作后的状态。
         * @return true 表示这一步导致游戏结束
         */
        fun flipCard(index: Int): Boolean {
            val card = cards[index]

            // 已配对或已翻开的牌，点击无效
            if (card.isMatched || card.isFlipped) return false

            card.isFlipped = true

            if (firstFlippedIndex == null) {
                // 这是本轮第一张
                firstFlippedIndex = index
            } else {
                // 这是本轮第二张，步数 +1
                moves++
                val firstIndex = firstFlippedIndex!!
                val firstCard = cards[firstIndex]

                if (firstCard.pairId == card.pairId) {
                    // 配对成功
                    firstCard.isMatched = true
                    card.isMatched = true
                    matchedPairs++
                    firstFlippedIndex = null

                    // 全部配对完成 = 游戏胜利
                    return matchedPairs == cards.size / 2
                } else {
                    // 配对失败：两张都翻回去
                    firstCard.isFlipped = false
                    card.isFlipped = false
                    firstFlippedIndex = null
                }
            }
            return false
        }
    }

    // ============ Controller 层 ============

    private lateinit var model: GameModel
    private val handler = Handler(Looper.getMainLooper())
    private var isProcessing = false  // 防止配对动画期间连续点击

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMemoryGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = GameModel()
        startNewGame()

        // 重新开始按钮
        binding.btnRestart.setOnClickListener {
            startNewGame()
        }
    }

    /** 开始一局新游戏：重置 Model + 重建 View */
    private fun startNewGame() {
        model.newGame()
        isProcessing = false
        buildCardViews()
        updateStatusBar()
    }

    // ============ View 层：构建与刷新 ============

    /** 根据 Model 生成 12 张牌的 Button 视图 */
    private fun buildCardViews() {
        binding.gridCards.removeAllViews()

        val margin = dpToPx(6)

        model.cards.forEachIndexed { index, card ->
            val btn = Button(this).apply {
                text = if (card.isFlipped) card.faceText else getString(R.string.card_back)
                textSize = 32f
                setBackgroundColor(
                    if (card.isMatched) getColor(R.color.card_matched_color)
                    else getColor(R.color.card_back_color)
                )
                setTextColor(Color.WHITE)
                setOnClickListener { onCardClicked(index, this) }
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = GridLayout.spec(index % 4, 1, 1f)  // 1列，权重1
                rowSpec = GridLayout.spec(index / 4, 1, 1f)     // 1行，权重1
                this.setMargins(margin, margin, margin, margin)
            }
            binding.gridCards.addView(btn, params)
        }
    }

    /** 单张牌被点击时的处理（Controller 核心逻辑） */
    private fun onCardClicked(index: Int, button: Button) {
        if (isProcessing) return
        val card = model.cards[index]
        if (card.isMatched || card.isFlipped) return

        // 翻牌动画：显示正面
        button.text = card.faceText
        button.setBackgroundColor(getColor(R.color.card_face_color))
        button.setTextColor(Color.BLACK)

        val gameWon = model.flipCard(index)
        updateStatusBar()

        if (gameWon) {
            // 延迟一下让用户看到最后一对配对成功
            handler.postDelayed({
                val msg = getString(R.string.game_win, model.moves)
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

                // 把步数作为最高分返回给 MainActivity
                val resultIntent = Intent().apply {
                    putExtra(getString(R.string.extra_high_score), model.moves)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }, 500)
        } else if (model.firstFlippedIndex == null && !card.isMatched) {
            // 配对失败：延迟 800ms 后把两张牌翻回去
            isProcessing = true
            handler.postDelayed({
                refreshAllCards()
                isProcessing = false
            }, 800)
        }
    }

    /** 根据 Model 状态刷新所有卡片的显示 */
    private fun refreshAllCards() {
        for (i in 0 until model.cards.size) {
            val btn = binding.gridCards.getChildAt(i) as? Button ?: continue
            val card = model.cards[i]
            btn.text = if (card.isFlipped) card.faceText else getString(R.string.card_back)
            btn.setBackgroundColor(
                when {
                    card.isMatched -> getColor(R.color.card_matched_color)
                    card.isFlipped -> getColor(R.color.card_face_color)
                    else -> getColor(R.color.card_back_color)
                }
            )
            btn.setTextColor(if (card.isFlipped) Color.BLACK else Color.WHITE)
        }
    }

    /** 更新顶部状态栏：步数 + 已配对数 */
    private fun updateStatusBar() {
        binding.tvMoves.text = getString(R.string.label_moves) + model.moves
        binding.tvPairs.text = getString(R.string.label_pairs) +
                "${model.matchedPairs}/${model.cards.size / 2}"
    }

    /** dp 转 px */
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
