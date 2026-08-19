package com.fireants.template.ui.component.level

import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.fireants.template.R
import com.fireants.template.databinding.ActivityLevelBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LevelActivity : BaseActivity<ActivityLevelBinding>() {

    private val viewModel: LevelViewModel by viewModels()
    private lateinit var levelAdapter: LevelAdapter

    override fun getLayoutActivity(): Int = R.layout.activity_level

    override fun initViews() {
        levelAdapter = LevelAdapter { levelItem ->
            Toast.makeText(this, "Clicked Level ${levelItem.level.levelNumber}", Toast.LENGTH_SHORT).show()
        }
        
        mBinding.rvLevels.layoutManager = GridLayoutManager(this, 3)
        mBinding.rvLevels.adapter = levelAdapter
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                // Update Player Top Bar
                state.player?.let { player ->
                    mBinding.tvCoins.text = "Coins: ${player.coins}"
                    mBinding.tvStars.text = "Stars: ${player.stars}"
                    mBinding.tvHints.text = "Hints: ${player.hints}"
                    mBinding.tvTickets.text = "Tickets: ${player.tickets}"
                }
                
                // Update Level List
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
}
