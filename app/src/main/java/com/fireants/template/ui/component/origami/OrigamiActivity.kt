package com.fireants.template.ui.component.origami

import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.fireants.template.R
import com.fireants.template.databinding.ActivityOrigamiBinding
import com.fireants.template.ui.bases.BaseActivity
import com.fireants.template.ui.bases.ext.click
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrigamiActivity : BaseActivity<ActivityOrigamiBinding>() {

    private val viewModel: ActivityOrigamiBinding by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_origami
    }

    override fun initViews() {
        super.initViews()
        mBinding.toolBar.tvTitle.text = getString(R.string.origami)
        mBinding.glowBackground.glowColor = ContextCompat.getColor(this, R.color.color_9779F4)
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.toolBar.imgBack.click {
            onBackPressed()
        }
    }

}