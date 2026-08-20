package com.fireants.template.ui.component.origami3d

import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.fireants.template.R
import com.fireants.template.databinding.ActivityOrigami3dBinding
import com.fireants.template.ui.bases.BaseActivity
import com.fireants.template.ui.bases.ext.click
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Origami3DActivity : BaseActivity<ActivityOrigami3dBinding>() {

    private val viewModel: Origami3DViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_origami_3d
    }

    override fun initViews() {
        super.initViews()
        mBinding.toolBar.tvTitle.text = getString(R.string.origami_3d)
        mBinding.glowBackground.glowColor = ContextCompat.getColor(this, R.color.color_5BC2FB)
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.toolBar.imgBack.click {
            onBackPressed()
        }
    }
}
