package com.pegas.origami.paper.folding.art.ui.component.shop

import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.ads.AdsManager
import com.pegas.origami.paper.folding.art.data.model.game.PaperItem
import com.pegas.origami.paper.folding.art.data.model.shop.BagReward
import com.pegas.origami.paper.folding.art.data.model.shop.ShopConfig
import com.pegas.origami.paper.folding.art.data.model.shop.ShopResult
import com.pegas.origami.paper.folding.art.databinding.ActivityShopBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseActivity
import com.pegas.origami.paper.folding.art.ui.bases.ext.click
import com.pegas.origami.paper.folding.art.ui.component.dialog.DialogPremium
import com.pegas.origami.paper.folding.art.utils.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopActivity : BaseActivity<ActivityShopBinding>(), ShopInteractionListener {

    private val viewModel: ShopViewModel by viewModels()

    override fun getLayoutActivity(): Int = R.layout.activity_shop

    private lateinit var shopAdapter: ShopMultiTypeAdapter

    override fun initViews() {
        AdsManager.loadInterBack(this)
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
            AdsManager.showInterBack(this) {
                onBackPressed()
            }
        }
    }



    override fun observeData() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                state.player?.let { player ->
                    mBinding.tvCoins.text = getString(R.string.number_format, player.coins)
                    mBinding.tvStars.text = getString(R.string.number_format, player.stars)
                    mBinding.tvHints.text = getString(R.string.number_format, player.hints)
                }

                val items = mutableListOf<ShopItem>()
//                items.add(ShopItem.GetCoins)
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
                            is ShopResult.Success -> getString(
                                R.string.purchase_success
                            )

                            is ShopResult.NotEnoughCoins -> getString(
                                R.string.not_enough_coins,
                                result.missingAmount
                            )

                            is ShopResult.NotEnoughStars -> getString(
                                R.string.not_enough_stars,
                                result.missingAmount
                            )

                            else -> result.toString()
                        }
                        Toast.makeText(this@ShopActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                    is ShopEvent.OnBagsOpened -> {
                        Toast.makeText(
                            this@ShopActivity,
                            formatBagRewardMessage(event.rewards),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun formatBagRewardMessage(rewards: List<BagReward>): String {
        if (rewards.size == 1) {
            return when (val reward = rewards.first()) {
                is BagReward.Coin -> getString(R.string.bag_reward_coin, reward.amount)
                is BagReward.Hint -> getString(R.string.bag_reward_hint, reward.amount)
                is BagReward.Paper -> getString(R.string.bag_reward_paper)
            }
        }

        val coins = rewards.filterIsInstance<BagReward.Coin>().sumOf { it.amount }
        val hints = rewards.filterIsInstance<BagReward.Hint>().sumOf { it.amount }
        val papers = rewards.count { it is BagReward.Paper }
        val summary = buildList {
            if (coins > 0) add(getString(R.string.bag_reward_coins_summary, coins))
            if (hints > 0) add(getString(R.string.bag_reward_hints_summary, hints))
            if (papers > 0) add(getString(R.string.bag_reward_papers_summary, papers))
        }.joinToString(", ")

        return getString(R.string.bag_reward_multi, summary)
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

    private fun buyWithCoinsOrShowPremium(cost: Int, onBuy: () -> Unit) {
        val currentCoins = viewModel.state.value.player?.coins ?: 0
        if (currentCoins >= cost) {
            onBuy()
        } else {
            DialogPremium(
                context = this,
                titleRes = R.string.shop_premium_title,
                messageRes = R.string.shop_premium_message
            ) {
                Routes.startIapActivity(this)
            }.show()
        }
    }

    override fun onBuy1HintClick() {
        buyWithCoinsOrShowPremium(250) {
            viewModel.buyHint(amount = 1, cost = 250)
        }
    }

    override fun onBuy3HintsClick() {
        buyWithCoinsOrShowPremium(637) {
            viewModel.buyHint(amount = 3, cost = 637)
        }
    }

    override fun onBuy5HintsClick() {
        buyWithCoinsOrShowPremium(1000) {
            viewModel.buyHint(amount = 5, cost = 1000)
        }
    }

    override fun onSelectPaperClick(paper: PaperItem) {
        viewModel.selectPaper(paper.id)
    }

    override fun onBuyPaperClick(paper: PaperItem) {
        val config = ShopConfig()
        buyWithCoinsOrShowPremium(config.paperUnlockCost) {
            viewModel.buyPaper(paper.id)
        }
    }
}
