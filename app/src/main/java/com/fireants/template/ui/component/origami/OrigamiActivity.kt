package com.fireants.template.ui.component.origami

import androidx.activity.viewModels
import com.fireants.template.R
import com.fireants.template.databinding.ActivityOrigamiBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrigamiActivity : BaseActivity<ActivityOrigamiBinding>() {

    private val viewModel: ActivityOrigamiBinding by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_origami
    }

}