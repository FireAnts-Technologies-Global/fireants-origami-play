package com.fireants.template.ui.component.shop

import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.fireants.template.R
import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.data.model.shop.ShopConfig
import com.fireants.template.databinding.ActivityShopBinding
import com.fireants.template.ui.bases.BaseActivity
import com.fireants.template.ui.bases.ext.click
import com.fireants.template.ui.component.dialog.DialogBuy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopActivity : BaseActivity<ActivityShopBinding>(), ShopInteractionListener {

    private val viewModel: ShopViewModel by viewModels()

    override fun getLayoutActivity(): Int = R.layout.activity_shop

    private lateinit var shopAdapter: ShopMultiTypeAdapter

    override fun initViews() {
        mBinding.tvHints.isSelected = true
        mBinding.tvStars.isSelected = true
        mBinding.tvCoins.isSelected = true
        mBinding.glowBackground.glowColor = ContextCompat.getColor(this, R.color.color_48D0B0)
        mBinding.glowBackground.baseBgColor = ContextCompat.getColor(this, R.color.color_0B0C1A)

        shopAdapter = ShopMultiTypeAdapter(this)

        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        mBinding.rvShop.layoutManager = layoutManager
        mBinding.rvShop.adapter = shopAdapter

        val animator = mBinding.rvShop.itemAnimator
        if (animator is androidx.recyclerview.widget.SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }


    override fun onClickViews() {
        super.onClickViews()
        mBinding.imgBack.click {
            onBackPressed()
        }
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

                if (state.papers.isNotEmpty()) {
                    items.add(ShopItem.PaperGroup(state.papers))
                }
                
                shopAdapter.submitList(items)
            }
        }
        
        lifecycleScope.launch {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is ShopEvent.ShowMessage -> {
                        val msg = when (val result = event.result) {
                            is com.fireants.template.data.model.shop.ShopResult.Success -> getString(
                                R.string.purchase_success
                            )

                            is com.fireants.template.data.model.shop.ShopResult.NotEnoughCoins -> getString(
                                R.string.not_enough_coins,
                                result.missingAmount
                            )

                            is com.fireants.template.data.model.shop.ShopResult.NotEnoughStars -> getString(
                                R.string.not_enough_stars,
                                result.missingAmount
                            )

                            else -> result.toString()
                        }
                        Toast.makeText(this@ShopActivity, msg, Toast.LENGTH_SHORT).show()
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

    private fun showBuyDialog(cost: Int, onBuy: () -> Unit) {
        val currentCoins = viewModel.state.value.player?.coins ?: 0
        DialogBuy(
            context = this,
            cost = cost,
            currentCoins = currentCoins,
            onBuyClick = onBuy,
            onAdsClick = {
                onWatchAdClick()
            }
        ).show()
    }

    override fun onBuy1HintClick() {
        showBuyDialog(250) {
            viewModel.buyHint(amount = 1, cost = 250)
        }
    }

    override fun onBuy3HintsClick() {
        showBuyDialog(637) {
            viewModel.buyHint(amount = 3, cost = 637)
        }
    }

    override fun onBuy5HintsClick() {
        showBuyDialog(1000) {
            viewModel.buyHint(amount = 5, cost = 1000)
        }
    }

    override fun onSelectPaperClick(paper: PaperItem) {
        viewModel.selectPaper(paper.id)
    }

    override fun onBuyPaperClick(paper: PaperItem) {
        val config = ShopConfig()
        showBuyDialog(config.paperUnlockCost) {
            viewModel.buyPaper(paper.id)
        }
    }
}
