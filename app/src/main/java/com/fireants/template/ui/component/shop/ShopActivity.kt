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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupRecyclerView()
        setupClickListeners()
        observeState()
        observeEvents()
    }

    private fun setupRecyclerView() {
        paperAdapter = PaperAdapter(
            onBuyClick = { paper ->
                viewModel.buyPaper(paper.id)
            },
            onSelectClick = { paper ->
                viewModel.selectPaper(paper.id)
            }
        )
        binding.rvPapers.layoutManager = GridLayoutManager(this, 3)
        binding.rvPapers.adapter = paperAdapter
    }

    private fun setupClickListeners() {
        binding.btnWatchAd.setOnClickListener {
            // Mock Ad Watch
            viewModel.addRewardedCoin()
            Toast.makeText(this, "Watched Ad! +300 Coins", Toast.LENGTH_SHORT).show()
        }

        binding.btnBuy1Hint.setOnClickListener {
            viewModel.buyHint(amount = 1, cost = 250)
        }
        
        binding.btnBuy3Hints.setOnClickListener {
            viewModel.buyHint(amount = 3, cost = 637)
        }
        
        binding.btnBuy5Hints.setOnClickListener {
            // Note: Using 1000 coins instead of 100 as 100 is likely a typo for 5 hints
            viewModel.buyHint(amount = 5, cost = 1000)
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
                
                paperAdapter.submitList(state.papers)
                
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
