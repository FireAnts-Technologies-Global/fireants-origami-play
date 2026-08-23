package com.fireants.template.ui.component.papercraft

import androidx.core.content.ContextCompat
import com.fireants.template.R
import com.fireants.template.databinding.ActivityPaperCraftBinding
import com.fireants.template.ui.bases.BaseActivity
import com.fireants.template.ui.bases.ext.click
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaperCraftActivity : BaseActivity<ActivityPaperCraftBinding>() {

    override fun getLayoutActivity(): Int {
        return R.layout.activity_paper_craft
    }

    override fun initViews() {
        super.initViews()
        val mode = PaperCraftMode.fromValue(intent.getStringExtra(EXTRA_MODE))

        mBinding.toolBar.tvTitle.text = getString(mode.titleRes)
        mBinding.glowBackground.glowColor = ContextCompat.getColor(this, mode.glowColorRes)
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.toolBar.imgBack.click {
            onBackPressed()
        }
    }

    enum class PaperCraftMode(
        val value: String,
        val titleRes: Int,
        val glowColorRes: Int
    ) {
        KIRIGAMI("kirigami", R.string.kirigami, R.color.color_48D0B0),
        ORIGAMI("origami", R.string.origami, R.color.color_9779F4),
        ORIGAMI_3D("origami_3d", R.string.origami_3d, R.color.color_5BC2FB);

        companion object {
            fun fromValue(value: String?): PaperCraftMode {
                return entries.firstOrNull { it.value == value } ?: ORIGAMI
            }
        }
    }

    companion object {
        const val EXTRA_MODE = "paper_craft_mode"
    }
}
