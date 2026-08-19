package com.fireants.template.ui.component.game

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fireants.template.R
import com.fireants.template.databinding.ActivityGameBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameActivity : BaseActivity<ActivityGameBinding>() {

    private val viewModel: GameViewModel by viewModels()
    
    // For calculating moves (each undo offsets a drag, we can just keep a simple counter)
    private var movesCount = 0

    override fun getLayoutActivity(): Int = R.layout.activity_game

    override fun initViews() {
        val levelId = intent.getIntExtra("LEVEL_ID", 1)
        viewModel.loadLevel(levelId)

        mBinding.btnReset.setOnClickListener {
            movesCount = 0
            mBinding.foldPaperView.resetPaperToFullSize()
        }

        mBinding.btnUndo.setOnClickListener {
            mBinding.foldPaperView.undoLastStep()
        }

        mBinding.btnHint.setOnClickListener {
            viewModel.useHint()
        }

        mBinding.foldPaperView.onLevelCompleted = { stars ->
            viewModel.completeLevel(stars, movesCount)
        }
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                // Update Top Bar
                state.player?.let { player ->
                    mBinding.tvCoins.text = "Coins: ${player.coins}"
                    mBinding.tvStars.text = "Stars: ${player.stars}"
                }
                
                state.currentLevel?.let { level ->
                    mBinding.tvLevelName.text = "Level ${level.levelNumber}"
                    // Set Target Points if available
                    if (level.targetPoints.isNotEmpty()) {
                        mBinding.foldPaperView.setLevelTarget(level.targetPoints.toFloatArray())
                    }
                }
                
                // Load Texture
                state.selectedPaper?.let { paper ->
                    loadTexture(paper.imagePreview)
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is GameEvent.ShowHint -> {
                        mBinding.foldPaperView.showSuggest()
                    }
                    is GameEvent.ShowError -> {
                        Toast.makeText(this@GameActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is GameEvent.LevelCompleted -> {
                        Toast.makeText(this@GameActivity, "Level Completed! ${event.stars} Stars, +${event.coinsEarned} Coins", Toast.LENGTH_LONG).show()
                        finish() // Exit game screen
                    }
                }
            }
        }
    }

    private var currentLoadedTexture: String? = null

    private fun loadTexture(texturePath: String) {
        if (texturePath == currentLoadedTexture) return
        
        Glide.with(this)
            .asBitmap()
            .load("file:///android_asset/$texturePath")
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    currentLoadedTexture = texturePath
                    mBinding.foldPaperView.setPaperBitmap(resource)
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}
