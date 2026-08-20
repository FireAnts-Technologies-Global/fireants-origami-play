package com.fireants.template.ui.component.kirigami

import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.fireants.template.R
import com.fireants.template.databinding.ActivityKirigamiBinding
import com.fireants.template.ui.bases.BaseActivity
import com.fireants.template.ui.bases.ext.click
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KirigamiActivity : BaseActivity<ActivityKirigamiBinding>() {

    private val viewModel: KirigamiViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_kirigami
    }

    override fun initViews() {
        super.initViews()
        mBinding.toolBar.tvTitle.text = getString(R.string.kirigami)
        mBinding.glowBackground.glowColor = ContextCompat.getColor(this, R.color.color_48D0B0)
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.toolBar.imgBack.click {
            onBackPressed()
        }
    }

}