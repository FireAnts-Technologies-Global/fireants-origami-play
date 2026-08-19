package com.fireants.template.ui.component.shop

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.fireants.template.R
import com.fireants.template.databinding.ActivityShopBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopActivity : BaseActivity<ActivityShopBinding>() {

    private val viewModel: ShopViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_shop
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupClickListeners()
        observeState()
        observeEvents()
    }

    private fun setupClickListeners() {
        binding.btnWatchAd.setOnClickListener {
            // Mock Ad Watch
            viewModel.addRewardedCoin()
            Toast.makeText(this, "Watched Ad! +300 Coins", Toast.LENGTH_SHORT).show()
        }

        binding.btnBuyHint.setOnClickListener {
            viewModel.buyHint(amount = 1, cost = 100)
        }

        binding.btnWatchAdBag.setOnClickListener {
            viewModel.buyBagWithAd()
        }
        
        binding.btnBuy1Bag.setOnClickListener {
            viewModel.buyBag(amount = 1, cost = 5)
        }
        
        binding.btnBuy10Bags.setOnClickListener {
            viewModel.buyBag(amount = 10, cost = 45)
        }

        binding.btnClaimFreeBag.setOnClickListener {
            viewModel.claimDailyBag()
        }

        binding.btnBuyPaper.setOnClickListener {
            // Mock picking a random locked paper to buy (Normally selected from a list)
            val lockedPapers = viewModel.state.value.papers.filter { !it.isUnlocked }
            if (lockedPapers.isNotEmpty()) {
                viewModel.buyPaper(lockedPapers.random().id)
            } else {
                Toast.makeText(this, "All papers are unlocked!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                state.player?.let { player ->
                    binding.tvCoins.text = "Coins: ${player.coins}"
                    binding.tvStars.text = "Stars: ${player.stars}"
                    binding.tvHints.text = "Hints: ${player.hints}"
                    binding.tvTickets.text = "Tickets: ${player.tickets}"
                }
                
                // Update free bag button state
                state.bagStatus?.let { status ->
                    if (status.canClaim) {
                        binding.btnClaimFreeBag.isEnabled = true
                        binding.btnClaimFreeBag.text = "Claim Free Daily Bag"
                    } else {
                        binding.btnClaimFreeBag.isEnabled = false
                        val hours = status.remainingMillis / (1000 * 60 * 60)
                        val mins = (status.remainingMillis % (1000 * 60 * 60)) / (1000 * 60)
                        binding.btnClaimFreeBag.text = "Next Free Bag in ${hours}h ${mins}m"
                    }
                }
            }
        }
    }

    private fun observeEvents() {
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
