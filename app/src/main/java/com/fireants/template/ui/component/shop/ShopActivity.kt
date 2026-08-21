package com.fireants.template.ui.component.shop

import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.fireants.template.R
import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.databinding.ActivityShopBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopActivity : BaseActivity<ActivityShopBinding>(), ShopInteractionListener {

    private val viewModel: ShopViewModel by viewModels()

    override fun getLayoutActivity(): Int = R.layout.activity_shop

    private lateinit var shopAdapter: ShopMultiTypeAdapter

    override fun initViews() {
        mBinding.glowBackground.glowColor = ContextCompat.getColor(this, R.color.color_48D0B0)
        mBinding.glowBackground.baseBgColor = ContextCompat.getColor(this, R.color.color_0B0C1A)

        shopAdapter = ShopMultiTypeAdapter(this)

        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (shopAdapter.getItemViewType(position)) {
                    R.layout.item_shop_paper -> 1
                    else -> 3
                }
            }
        }
        mBinding.rvShop.layoutManager = layoutManager
        mBinding.rvShop.adapter = shopAdapter
    }

    override fun onClickViews() {
        // Events handled by ShopInteractionListener
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                state.player?.let { player ->
                    mBinding.tvCoins.text = "${player.coins}"
                    mBinding.tvStars.text = "${player.stars}"
                    mBinding.tvHints.text = "${player.hints}"
                }

                val items = mutableListOf<ShopItem>()
                items.add(ShopItem.GetCoins)
                items.add(ShopItem.LuckyBag(state.bagStatus))
                items.add(ShopItem.BuyHints)
                items.add(ShopItem.PaperTitle)
                items.addAll(state.papers.map { ShopItem.Paper(it) })

                shopAdapter.submitList(items)
            }
        }
        
        lifecycleScope.launch {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is ShopEvent.ShowMessage -> {
                        Toast.makeText(this@ShopActivity, event.result.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is ShopEvent.OnBagsOpened -> {
                        Toast.makeText(this@ShopActivity, "Opened ${event.rewards.size} bags! Got: ${event.rewards}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onWatchAdClick() {
        viewModel.addRewardedCoin()
        Toast.makeText(this, "Watched Ad! +300 Coins", Toast.LENGTH_SHORT).show()
    }

    override fun onClaimFreeBagClick() {
        viewModel.claimDailyBag()
    }

    override fun onWatchAdBagClick() {
        viewModel.buyBagWithAd()
    }

    override fun onBuy1BagClick() {
        viewModel.buyBag(amount = 1, cost = 5)
    }

    override fun onBuy10BagsClick() {
        viewModel.buyBag(amount = 10, cost = 45)
    }

    override fun onBuy1HintClick() {
        viewModel.buyHint(amount = 1, cost = 250)
    }

    override fun onBuy3HintsClick() {
        viewModel.buyHint(amount = 3, cost = 637)
    }

    override fun onBuy5HintsClick() {
        viewModel.buyHint(amount = 5, cost = 1000)
    }

    override fun onSelectPaperClick(paper: PaperItem) {
        viewModel.selectPaper(paper.id)
    }

    override fun onBuyPaperClick(paper: PaperItem) {
        viewModel.buyPaper(paper.id)
    }
}
