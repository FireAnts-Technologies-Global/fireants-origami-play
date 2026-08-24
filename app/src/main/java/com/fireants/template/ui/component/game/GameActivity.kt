package com.fireants.template.ui.component.game

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fireants.template.R
import com.fireants.template.databinding.ActivityGameBinding
import com.fireants.template.ui.bases.BaseActivity
import com.fireants.template.ui.bases.ext.showRateDialog
import com.fireants.template.ui.component.custom.FoldPaperView.AutoFoldStep
import com.fireants.template.ui.component.dialog.DialogComplete
import com.fireants.template.ui.component.dialog.DialogLoading
import com.fireants.template.utils.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameActivity : BaseActivity<ActivityGameBinding>() {

    private val viewModel: GameViewModel by viewModels()
    
    private var movesCount = 0
    private var currentLevelId = 1
    private var isLevelCompleted = false
    private var completeDialog: DialogComplete? = null
    private var loadingDialog: DialogLoading? = null
    private var completeDialogJob: Job? = null
    private var rateDialogJob: Job? = null

    override fun getLayoutActivity(): Int = R.layout.activity_game

    override fun initViews() {
        currentLevelId = intent.getIntExtra("LEVEL_ID", 1)
        startLevel(currentLevelId)
        updateActionButtons()

        mBinding.imgBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.imgStore.setOnClickListener {
            Routes.startShopActivity(this)
        }

        mBinding.btnReset.setOnClickListener {
            if (movesCount <= 0) return@setOnClickListener
            movesCount = 0
            mBinding.foldPaperView.resetPaperToFullSize()
            updateActionButtons()
        }

        mBinding.btnUndo.setOnClickListener {
            if (movesCount <= 0) return@setOnClickListener
            mBinding.foldPaperView.undoLastStep()
        }

        mBinding.btnHint.setOnClickListener {
            viewModel.useHint()
        }

        mBinding.foldPaperView.onLevelCompleted = { stars ->
            if (!isLevelCompleted) {
                isLevelCompleted = true
                viewModel.completeLevel(stars, movesCount)
            }
        }

        mBinding.foldPaperView.onFoldHistoryChanged = { foldCount ->
            movesCount = foldCount
            updateActionButtons()
        }
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                state.player?.let { player ->
                    mBinding.tvCoins.text = getString(R.string.number_format, player.coins)
                    mBinding.tvHintCount.text = getString(R.string.number_format, player.hints)
                }
                updateActionButtons()
                
                state.currentLevel?.let { level ->
                    mBinding.tvTitle.text = getString(R.string.level_title, level.levelNumber)
                    if (level.targetPoints.isNotEmpty()) {
                        mBinding.foldPaperView.setLevelTarget(level.targetPoints.toFloatArray())
                    }
                }
                
                state.selectedPaper?.let { paper ->
                    loadTexture(paper.imagePreview)
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is GameEvent.ShowHint -> {
                        val hints = viewModel.state.value.foldHints
                        if (hints.isNotEmpty() && movesCount < hints.size) {
                            val hint = hints[movesCount]
                            val step = AutoFoldStep(hint.startXRatio, hint.startYRatio, hint.endXRatio, hint.endYRatio)
                            mBinding.foldPaperView.startAutoFold(step)
                            movesCount++
                            updateActionButtons()
                        } else {
                            mBinding.foldPaperView.showSuggest()
                        }
                    }
                    is GameEvent.OpenStore -> {
                        Routes.startShopActivity(this@GameActivity)
                    }
                    is GameEvent.ShowError -> {
                        Toast.makeText(this@GameActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is GameEvent.LevelCompleted -> {
                        showCompleteDialogAfterLoading(event.stars, event.coinsEarned)
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

    private fun updateActionButtons() {
        val hintsCount = viewModel.state.value.player?.hints ?: 0
        val hasNextHintStep = movesCount < viewModel.state.value.foldHints.size

        setActionButtonState(mBinding.btnReset, movesCount > 0)
        setActionButtonState(mBinding.btnUndo, movesCount > 0)
        setActionButtonState(mBinding.btnHint, hintsCount == 0 || hasNextHintStep)
    }

    private fun setActionButtonState(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        view.alpha = if (enabled) 1f else 0.45f
    }

    private fun startLevel(levelId: Int) {
        completeDialogJob?.cancel()
        loadingDialog?.dismiss()
        completeDialog?.dismiss()
        currentLevelId = levelId
        movesCount = 0
        isLevelCompleted = false
        mBinding.foldPaperView.resetPaperToFullSize()
        updateActionButtons()
        viewModel.loadLevel(levelId)
    }

    private fun showCompleteDialogAfterLoading(stars: Int, coinsEarned: Int) {
        if (loadingDialog?.isShowing == true || completeDialog?.isShowing == true) return

        loadingDialog = DialogLoading(this)
        loadingDialog?.show()
        completeDialogJob?.cancel()
        completeDialogJob = lifecycleScope.launch {
            delay(COMPLETE_LOADING_DELAY_MS)
            loadingDialog?.dismiss()
            showCompleteDialog(stars, coinsEarned)
        }
    }

    private fun showCompleteDialog(stars: Int, coinsEarned: Int) {
        if (completeDialog?.isShowing == true) return

        completeDialog = DialogComplete(
            context = this,
            stars = stars,
            coinsEarned = coinsEarned,
            onPlayAgainClick = {
                scheduleRateDialogAfterGameCompleteIfNeeded()
                startLevel(currentLevelId)
            },
            onContinueClick = {
                val nextLevelId = viewModel.state.value.nextLevelId
                if (nextLevelId == null) {
                    finish()
                } else {
                    scheduleRateDialogAfterGameCompleteIfNeeded()
                    startLevel(nextLevelId)
                }
            }
        )
        completeDialog?.show()
    }

    private fun scheduleRateDialogAfterGameCompleteIfNeeded() {
        if (
            appSharedPref.isRate ||
            appSharedPref.isRateShownInSession ||
            rateDialogJob?.isActive == true
        ) {
            return
        }

        appSharedPref.isRateShownInSession = true
        rateDialogJob = lifecycleScope.launch {
            delay(RATE_DIALOG_AFTER_COMPLETE_DELAY_MS)
            showRateDialog(this@GameActivity, false) {
                appSharedPref.isRate = true
            }
        }
    }

    override fun onDestroy() {
        completeDialogJob?.cancel()
        rateDialogJob?.cancel()
        loadingDialog?.dismiss()
        loadingDialog = null
        completeDialog?.dismiss()
        completeDialog = null
        super.onDestroy()
    }

    companion object {
        private const val COMPLETE_LOADING_DELAY_MS = 2_500L
        private const val RATE_DIALOG_AFTER_COMPLETE_DELAY_MS = 300L
    }
}
