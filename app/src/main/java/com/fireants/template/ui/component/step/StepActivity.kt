package com.fireants.template.ui.component.step

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fireants.template.R
import com.fireants.template.data.model.product.GameType
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.databinding.ActivityStepBinding
import com.fireants.template.ui.bases.BaseActivity
import com.fireants.template.ui.bases.ext.click
import com.fireants.template.ui.bases.ext.showRateDialog
import com.fireants.template.utils.AppAudioController
import com.fireants.template.utils.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StepActivity : BaseActivity<ActivityStepBinding>(){
    private val viewModel: StepViewModel by viewModels()
    private val audioController by lazy { AppAudioController(this, appSharedPref) }
    private var autoResultJob: Job? = null
    private var rateDialogJob: Job? = null
    private var resultOpened = false
    private var currentPreviewImage: String? = null

    override fun getLayoutActivity(): Int {
        return R.layout.activity_step
    }

    override fun initViews() {
        super.initViews()
        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, 0)
        val favoriteId = intent.getIntExtra(EXTRA_FAVORITE_ID, 0)
        val gameType = GameType.entries.firstOrNull {
            it.name == intent.getStringExtra(EXTRA_GAME_TYPE)
        } ?: GameType.ORIGAMI
        val productName = intent.getStringExtra(EXTRA_PRODUCT_NAME).orEmpty()
        val productImage = intent.getStringExtra(EXTRA_PRODUCT_IMAGE).orEmpty()
        val difficulty = intent.getStringExtra(EXTRA_DIFFICULTY).orEmpty()
        val stepCount = intent.getIntExtra(EXTRA_STEP_COUNT, 0)
        val estimatedTime = intent.getStringExtra(EXTRA_ESTIMATED_TIME).orEmpty()

        if (productId > 0) {
            viewModel.load(productId, gameType)
        }
        viewModel.loadFavorite(
            favoriteId = favoriteId,
            sourceId = productId,
            gameType = gameType,
            name = productName,
            image = productImage,
            difficulty = difficulty,
            stepCount = stepCount,
            estimatedTime = estimatedTime
        )
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.imgBack.click {
            audioController.playClickSound()
            onBackPressed()
        }
        mBinding.imgStore.click {
            audioController.playClickSound()
            Routes.startShopActivity(this)
        }
        mBinding.imgFavourite.click {
            audioController.playClickSound()
            viewModel.toggleFavorite()
        }
        mBinding.btnLeft.click {
            audioController.playClickSound()
            cancelAutoResult()
            viewModel.previousStep()
        }
        mBinding.btnRight.click {
            val state = viewModel.state.value
            if (state.steps.isEmpty()) return@click
            audioController.playClickSound()
            if (state.currentIndex >= state.steps.lastIndex) {
                openResult(state)
            } else {
                cancelAutoResult()
                viewModel.nextStep()
            }
        }
    }

    override fun observeData() {
        super.observeData()
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                val currentStep = state.currentStep ?: return@collect
                mBinding.tvTitle.text = getString(
                    R.string.step_progress,
                    state.currentIndex + 1,
                    state.steps.size
                )
                mBinding.imgFavourite.setImageResource(
                    if (state.isFavorite) R.drawable.ic_favourite_on else R.drawable.ic_favourite_off
                )

                loadStepImageIfNeeded(currentStep.image, state)
                scheduleRateDialogIfNeeded(state)
                scheduleAutoResultIfNeeded(state)
            }
        }
    }

    private fun loadStepImageIfNeeded(image: String, state: StepState) {
        if (currentPreviewImage == image) {
            preloadNeighborImages(state)
            return
        }

        currentPreviewImage = image
        mBinding.imgPreview.resetTransformOnNextImage()
        Glide.with(this@StepActivity)
            .load(ASSET_PREFIX + image)
            .dontAnimate()
            .fitCenter()
            .into(mBinding.imgPreview)
        preloadNeighborImages(state)
    }

    private fun preloadNeighborImages(state: StepState) {
        listOf(state.currentIndex - 1, state.currentIndex + 1)
            .mapNotNull { state.steps.getOrNull(it)?.image }
            .forEach { image ->
                Glide.with(this@StepActivity)
                    .load(ASSET_PREFIX + image)
                    .dontAnimate()
                    .preload()
            }
    }

    private fun scheduleRateDialogIfNeeded(state: StepState) {
        val stepNumber = state.currentIndex + 1
        if (
            stepNumber !in RATE_DIALOG_STEP_RANGE ||
            appSharedPref.isRate ||
            appSharedPref.isRateShownInSession ||
            rateDialogJob?.isActive == true
        ) {
            return
        }

        rateDialogJob = lifecycleScope.launch {
            delay(RATE_DIALOG_DELAY_MS)
            appSharedPref.isRateShownInSession = true
            showRateDialog(this@StepActivity, false) {
                appSharedPref.isRate = true
            }
        }
    }

    private fun scheduleAutoResultIfNeeded(state: StepState) {
        if (state.steps.isEmpty() || state.currentIndex < state.steps.lastIndex || resultOpened) {
            cancelAutoResult()
            return
        }
        if (autoResultJob?.isActive == true) return

        autoResultJob = lifecycleScope.launch {
            delay(AUTO_RESULT_DELAY_MS)
            openResult(viewModel.state.value)
        }
    }

    private fun cancelAutoResult() {
        autoResultJob?.cancel()
        autoResultJob = null
    }

    private fun cancelRateDialog() {
        rateDialogJob?.cancel()
        rateDialogJob = null
    }

    private fun openResult(state: StepState) {
        if (resultOpened || state.steps.isEmpty()) return
        resultOpened = true
        cancelAutoResult()
        Routes.startResultActivity(this, state.toProductItem())
    }

    override fun onResume() {
        super.onResume()
        audioController.playBackgroundMusic()
    }

    override fun onPause() {
        audioController.pauseBackgroundMusic()
        super.onPause()
    }

    override fun onDestroy() {
        cancelAutoResult()
        cancelRateDialog()
        audioController.release()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_FAVORITE_ID = "extra_favorite_id"
        const val EXTRA_GAME_TYPE = "extra_game_type"
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        const val EXTRA_PRODUCT_IMAGE = "extra_product_image"
        const val EXTRA_DIFFICULTY = "extra_difficulty"
        const val EXTRA_STEP_COUNT = "extra_step_count"
        const val EXTRA_ESTIMATED_TIME = "extra_estimated_time"
        private const val ASSET_PREFIX = "file:///android_asset/"
        private const val AUTO_RESULT_DELAY_MS = 3_000L
        private const val RATE_DIALOG_DELAY_MS = 2_000L
        private val RATE_DIALOG_STEP_RANGE = 7..8
    }
}

private fun StepState.toProductItem() = ProductItem(
    id = favoriteId,
    sourceId = sourceId,
    categoryId = 0,
    name = name,
    image = image,
    order = 0,
    gameType = gameType,
    difficulty = difficulty,
    stepCount = stepCount,
    estimatedTime = estimatedTime,
    isFavorite = isFavorite
)
