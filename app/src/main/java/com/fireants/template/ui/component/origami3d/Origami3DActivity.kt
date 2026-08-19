package com.fireants.template.ui.component.origami3d

import androidx.activity.viewModels
import com.fireants.template.R
import com.fireants.template.databinding.ActivityOrigami3dBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Origami3DActivity : BaseActivity<ActivityOrigami3dBinding>() {

    private val viewModel: Origami3DViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_origami_3d
    }
}
