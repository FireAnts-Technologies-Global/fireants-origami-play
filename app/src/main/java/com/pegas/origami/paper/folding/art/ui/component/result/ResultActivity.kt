package com.pegas.origami.paper.folding.art.ui.component.result

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.ads.AdRemoteConfig
import com.pegas.origami.paper.folding.art.ads.banner_all
import com.pegas.origami.paper.folding.art.data.model.product.GameType
import com.pegas.origami.paper.folding.art.databinding.ActivityResultBinding
import com.pegas.origami.paper.folding.art.ui.bases.BannerConfig
import com.pegas.origami.paper.folding.art.ui.bases.BaseActivityWithBanner
import com.pegas.origami.paper.folding.art.ui.bases.ext.click
import com.pegas.origami.paper.folding.art.ui.bases.ext.showRateDialog
import com.pegas.origami.paper.folding.art.ui.component.main.ProductDisplayFormatter
import com.pegas.origami.paper.folding.art.utils.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResultActivity : BaseActivityWithBanner<ActivityResultBinding>() {

    override val bannerConfig = BannerConfig(AdRemoteConfig.banner_all, false)

    private val viewModel: ResultViewModel by viewModels()
    private var exploreDestinations: List<ExploreDestination> = emptyList()

    private val likeAdapter by lazy {
        ResultLikeAdapter(
            onItemClick = { Routes.startStepActivity(this, it) },
            onFavoriteClick = { viewModel.toggleFavorite(it) }
        )
    }

    override fun getLayoutActivity(): Int = R.layout.activity_result

    override fun initViews() {
        super.initViews()
        if (!appSharedPref.isRate && !appSharedPref.isRateShownInSession) {
            lifecycleScope.launch {
                delay(3000)
                appSharedPref.isRateShownInSession = true
                showRateDialog(this@ResultActivity, false) {
                    appSharedPref.isRate = true
                }
            }
        }
        mBinding.rvYouMayLike.apply {
            layoutManager = LinearLayoutManager(
                this@ResultActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = likeAdapter
        }
        viewModel.load(
            favoriteId = intent.getIntExtra(EXTRA_FAVORITE_ID, 0),
            sourceId = intent.getIntExtra(EXTRA_PRODUCT_ID, 0),
            gameType = GameType.entries.firstOrNull {
                it.name == intent.getStringExtra(EXTRA_GAME_TYPE)
            } ?: GameType.ORIGAMI,
            name = intent.getStringExtra(EXTRA_PRODUCT_NAME).orEmpty(),
            image = intent.getStringExtra(EXTRA_PRODUCT_IMAGE).orEmpty(),
            difficulty = intent.getStringExtra(EXTRA_DIFFICULTY).orEmpty(),
            stepCount = intent.getIntExtra(EXTRA_STEP_COUNT, 0),
            estimatedTime = intent.getStringExtra(EXTRA_ESTIMATED_TIME).orEmpty()
        )
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.imgBack.click { finish() }
        mBinding.imgHome.click { Routes.startMainActivity(this) }
        mBinding.layoutOrigami.click { openExploreDestination(0) }
        mBinding.layoutKirigami.click { openExploreDestination(1) }
        mBinding.layoutGame.click { openExploreDestination(2) }
        mBinding.tvViewAll.click { Routes.startRecommendedActivity(this) }
        mBinding.btnShareCreation.click { shareCreation() }
    }

    override fun observeData() {
        super.observeData()
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                val item = state.completedItem ?: return@collect
                mBinding.tvProductName.text = ProductDisplayFormatter.name(item)
                if (item.image.isNotEmpty()) {
                    Glide.with(this@ResultActivity)
                        .load(item.image)
                        .into(mBinding.imgResult)
                }
                renderExploreMore(item.gameType)
                likeAdapter.submitList(state.suggestions)
            }
        }
    }

    private fun renderExploreMore(completedGameType: GameType) {
        exploreDestinations = ExploreDestination.entries
            .filterNot { it.gameType == completedGameType }
            .take(EXPLORE_SLOT_COUNT)

        val imageViews = listOf(
            mBinding.imgExploreOne,
            mBinding.imgExploreTwo,
            mBinding.imgExploreThree
        )
        val textViews = listOf(
            mBinding.tvExploreOne,
            mBinding.tvExploreTwo,
            mBinding.tvExploreThree
        )

        exploreDestinations.forEachIndexed { index, destination ->
            imageViews[index].setImageResource(destination.imageRes)
            textViews[index].setText(destination.titleRes)
        }
    }

    private fun openExploreDestination(index: Int) {
        when (exploreDestinations.getOrNull(index)) {
            ExploreDestination.ORIGAMI -> Routes.startOrigamiActivity(this)
            ExploreDestination.ORIGAMI_3D -> Routes.startOrigami3DActivity(this)
            ExploreDestination.KIRIGAMI -> Routes.startKirigamiActivity(this)
            ExploreDestination.GAME -> Routes.startLevelActivity(this)
            null -> Unit
        }
    }

    private fun shareCreation() {
        val item = viewModel.state.value.completedItem ?: return
        val message = getString(
            R.string.share_creation_message,
            ProductDisplayFormatter.name(item),
            getString(R.string.app_name)
        )
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(this, getString(R.string.share_to)))
        }
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
        private const val EXPLORE_SLOT_COUNT = 3
    }
}

private enum class ExploreDestination(
    val gameType: GameType?,
    val titleRes: Int,
    val imageRes: Int
) {
    ORIGAMI(GameType.ORIGAMI, R.string.origami, R.drawable.img_origami),
    ORIGAMI_3D(GameType.ORIGAMI_3D, R.string.mode_3d, R.drawable.img_3d),
    KIRIGAMI(GameType.KIRIGAMI, R.string.kirigami, R.drawable.img_kirigami),
    GAME(null, R.string.game, R.drawable.img_game)
}
