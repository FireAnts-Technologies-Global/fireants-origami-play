package com.pegas.origami.paper.folding.art.ui.component.level

import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.ads.AdRemoteConfig
import com.pegas.origami.paper.folding.art.ads.AdsManager
import com.pegas.origami.paper.folding.art.ads.banner_all
import com.pegas.origami.paper.folding.art.databinding.ActivityLevelBinding
import com.pegas.origami.paper.folding.art.ui.bases.BannerConfig
import com.pegas.origami.paper.folding.art.ui.bases.BaseActivityWithBanner
import com.pegas.origami.paper.folding.art.ui.bases.ext.click
import com.pegas.origami.paper.folding.art.ui.component.custom.GridSpacingItemDecoration
import com.pegas.origami.paper.folding.art.ui.component.game.GameActivity
import com.pegas.origami.paper.folding.art.utils.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LevelActivity : BaseActivityWithBanner<ActivityLevelBinding>() {
    override val bannerConfig = BannerConfig(AdRemoteConfig.banner_all, false)

    private val viewModel: LevelViewModel by viewModels()
    private lateinit var levelAdapter: LevelAdapter

    override fun getLayoutActivity(): Int = R.layout.activity_level

    override fun initViews() {
        AdsManager.loadInterBack(this)
        mBinding.tvTitle.text = getString(R.string.game)
        mBinding.glowBackground.glowColor = ContextCompat.getColor(this, R.color.color_FF9E44)
        levelAdapter = LevelAdapter { levelItem ->
            val intent = Intent(this@LevelActivity, GameActivity::class.java).apply {
                putExtra("LEVEL_ID", levelItem.level.id)
            }
            startActivity(intent)
        }

        val spanCount = 4
        val spacingInPx = resources.getDimensionPixelSize(R.dimen.space_level)

        mBinding.rvLevels.apply {
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = levelAdapter
            addItemDecoration(GridSpacingItemDecoration(spanCount, spacingInPx, includeEdge = true))
        }
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.imgBack.click {
            AdsManager.showInterBack(this) {
                onBackPressed()
            }
        }
        mBinding.imgStore.click {
            Routes.startShopActivity(this)
        }
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                state.player?.let { player ->
                    mBinding.tvCoins.text = getString(R.string.number_format, player.coins)
                    mBinding.tvStars.text = getString(R.string.number_format, player.stars)
                }
                
                levelAdapter.submitList(state.levelItems)
            }
        }
        
        lifecycleScope.launch {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is LevelEvent.ShowError -> {
                        Toast.makeText(this@LevelActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }
}
