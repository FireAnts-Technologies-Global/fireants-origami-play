package com.pegas.origami.paper.folding.art.ui.component.papercraft

import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.ads.AdRemoteConfig
import com.pegas.origami.paper.folding.art.ads.banner_all
import com.pegas.origami.paper.folding.art.data.model.product.GameType
import com.pegas.origami.paper.folding.art.databinding.ActivityPaperCraftBinding
import com.pegas.origami.paper.folding.art.domain.model.product.HomeProductSection
import com.pegas.origami.paper.folding.art.ui.bases.BannerConfig
import com.pegas.origami.paper.folding.art.ui.bases.BaseActivityWithBanner
import com.pegas.origami.paper.folding.art.ui.bases.ext.click
import com.pegas.origami.paper.folding.art.ui.component.custom.GridSpacingItemDecoration
import com.pegas.origami.paper.folding.art.utils.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaperCraftActivity : BaseActivityWithBanner<ActivityPaperCraftBinding>() {
    override val bannerConfig = BannerConfig(AdRemoteConfig.banner_all, false)

    private val viewModel: PaperCraftViewModel by viewModels()
    private lateinit var adapter: PaperCraftAdapter
    private lateinit var mode: PaperCraftMode

    override fun getLayoutActivity(): Int {
        return R.layout.activity_paper_craft
    }

    override fun initViews() {
        super.initViews()
        mode = PaperCraftMode.fromValue(intent.getStringExtra(EXTRA_MODE))

        mBinding.toolBar.tvTitle.text = getString(mode.titleRes)
        mBinding.glowBackground.glowColor = ContextCompat.getColor(this, mode.glowColorRes)

        adapter = PaperCraftAdapter(
            onItemClick = { item -> Routes.startStepActivity(this, item) },
            onFavoriteClick = { item -> viewModel.toggleFavorite(item) }
        )
        mBinding.rvItems.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        mBinding.rvItems.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = SPAN_COUNT,
                spacing = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
            )
        )
        mBinding.rvItems.adapter = adapter
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.toolBar.imgBack.click {
            onBackPressed()
        }
        mBinding.searchBar.edtInput.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text?.toString().orEmpty())
        }
    }

    override fun onResume() {
        super.onResume()
        if (::mode.isInitialized) {
            mode.gameType?.let { viewModel.load(it) }
            mode.section?.let { viewModel.load(it) }
        }
    }

    override fun observeData() {
        super.observeData()
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                adapter.submitList(state.visibleItems)
            }
        }
    }

    enum class PaperCraftMode(
        val value: String,
        val titleRes: Int,
        val glowColorRes: Int,
        val gameType: GameType? = null,
        val section: HomeProductSection? = null
    ) {
        KIRIGAMI("kirigami", R.string.kirigami, R.color.color_48D0B0, gameType = GameType.KIRIGAMI),
        ORIGAMI("origami", R.string.origami, R.color.color_9779F4, gameType = GameType.ORIGAMI),
        ORIGAMI_3D("origami_3d", R.string.origami_3d, R.color.color_5BC2FB, gameType = GameType.ORIGAMI_3D),
        RECOMMENDED(
            "recommended",
            R.string.recommended_for_you,
            R.color.color_9779F4,
            section = HomeProductSection.RECOMMENDED
        ),
        HOT("hot", R.string.hot, R.color.color_5BC2FB, section = HomeProductSection.HOT),
        FAVORITES("favorites", R.string.favorites, R.color.color_48D0B0, section = HomeProductSection.FAVORITES);

        companion object {
            fun fromValue(value: String?): PaperCraftMode {
                return entries.firstOrNull { it.value == value } ?: ORIGAMI
            }
        }
    }

    companion object {
        const val EXTRA_MODE = "paper_craft_mode"
        private const val SPAN_COUNT = 2
    }
}
