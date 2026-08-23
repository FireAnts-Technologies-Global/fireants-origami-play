package com.fireants.template.ui.component.step

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fireants.template.R
import com.fireants.template.data.model.product.GameType
import com.fireants.template.databinding.ActivityStepBinding
import com.fireants.template.ui.bases.BaseActivity
import com.fireants.template.ui.bases.ext.click
import com.fireants.template.utils.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StepActivity : BaseActivity<ActivityStepBinding>(){
    private val viewModel: StepViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_step
    }

    override fun initViews() {
        super.initViews()
        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, 0)
        val gameType = GameType.entries.firstOrNull {
            it.name == intent.getStringExtra(EXTRA_GAME_TYPE)
        } ?: GameType.ORIGAMI

        if (productId > 0) {
            viewModel.load(productId, gameType)
        }
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.imgBack.click {
            onBackPressed()
        }
        mBinding.imgStore.click {
            Routes.startShopActivity(this)
        }
        mBinding.btnLeft.click {
            viewModel.previousStep()
        }
        mBinding.btnRight.click {
            viewModel.nextStep()
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

                Glide.with(this@StepActivity)
                    .load(ASSET_PREFIX + currentStep.image)
                    .fitCenter()
                    .into(mBinding.imgPreview)
            }
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_GAME_TYPE = "extra_game_type"
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        private const val ASSET_PREFIX = "file:///android_asset/"
    }
}
