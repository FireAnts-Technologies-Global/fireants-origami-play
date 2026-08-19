package com.fireants.template.ui.component.game

import androidx.activity.viewModels
import com.fireants.template.R
import com.fireants.template.databinding.ActivityGameBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : BaseActivity<ActivityGameBinding>() {

    private val viewModel: GameViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_game
    }
}