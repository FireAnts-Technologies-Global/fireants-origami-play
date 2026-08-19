package com.fireants.template.ui.component.level

import androidx.activity.viewModels
import com.fireants.template.R
import com.fireants.template.databinding.ActivityLevelBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LevelActivity : BaseActivity<ActivityLevelBinding>() {

    private val viewModel: LevelViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_level
    }

}