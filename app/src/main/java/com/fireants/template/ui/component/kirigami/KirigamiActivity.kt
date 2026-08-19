package com.fireants.template.ui.component.kirigami

import androidx.activity.viewModels
import com.fireants.template.R
import com.fireants.template.databinding.ActivityKirigamiBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KirigamiActivity : BaseActivity<ActivityKirigamiBinding>() {

    private val viewModel: KirigamiViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_kirigami
    }

}