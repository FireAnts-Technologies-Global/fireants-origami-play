package com.fireants.template.ui.component.shop

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.fireants.template.R
import com.fireants.template.databinding.ActivityShopBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopActivity : BaseActivity<ActivityShopBinding>() {

    private val viewModel: ShopViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_shop
    }

    private lateinit var paperAdapter: PaperAdapter

    override fun initViews() {
        mBinding.viewModel = viewModel
        
        paperAdapter = PaperAdapter(
            onBuyClick = { paper ->
                viewModel.buyPaper(paper.id)
            },
            onSelectClick = { paper ->
                viewModel.selectPaper(paper.id)
            }
        )
        mBinding.rvPapers.layoutManager = GridLayoutManager(this, 3)
        mBinding.rvPapers.adapter = paperAdapter
    }

    override fun onClickViews() {
        mBinding.btnWatchAd.setOnClickListener {
            // Mock Ad Watch
            viewModel.addRewardedCoin()
            Toast.makeText(this, "Watched Ad! +300 Coins", Toast.LENGTH_SHORT).show()
        }

        mBinding.btnBuy1Hint.setOnClickListener {
            viewModel.buyHint(amount = 1, cost = 250)
        }
        
        mBinding.btnBuy3Hints.setOnClickListener {
            viewModel.buyHint(amount = 3, cost = 637)
        }
        
        mBinding.btnBuy5Hints.setOnClickListener {
            viewModel.buyHint(amount = 5, cost = 1000)
        }

        mBinding.btnWatchAdBag.setOnClickListener {
            viewModel.buyBagWithAd()
        }
        
        mBinding.btnBuy1Bag.setOnClickListener {
            viewModel.buyBag(amount = 1, cost = 5)
        }
        
        mBinding.btnBuy10Bags.setOnClickListener {
            viewModel.buyBag(amount = 10, cost = 45)
        }

        mBinding.btnClaimFreeBag.setOnClickListener {
            viewModel.claimDailyBag()
        }
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                state.player?.let { player ->
                    mBinding.tvCoins.text = "Coins: ${player.coins}"
                    mBinding.tvStars.text = "Stars: ${player.stars}"
                    mBinding.tvHints.text = "Hints: ${player.hints}"
                    mBinding.tvTickets.text = "Tickets: ${player.tickets}"
                }
                
                paperAdapter.submitList(state.papers)
                
                // Update free bag button state
                state.bagStatus?.let { status ->
                    if (status.canClaim) {
                        mBinding.btnClaimFreeBag.isEnabled = true
                        mBinding.btnClaimFreeBag.text = "Claim Free Daily Bag"
                    } else {
                        mBinding.btnClaimFreeBag.isEnabled = false
                        val hours = status.remainingMillis / (1000 * 60 * 60)
                        val mins = (status.remainingMillis % (1000 * 60 * 60)) / (1000 * 60)
                        mBinding.btnClaimFreeBag.text = "Next Free Bag in ${hours}h ${mins}m"
                    }
                }
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
}
