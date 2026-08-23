package com.fireants.template.ui.component.dialog

import android.content.Context
import com.fireants.template.R
import com.fireants.template.databinding.DialogComplateBinding
import com.fireants.template.ui.bases.BaseDialog
import com.fireants.template.ui.bases.ext.click

class DialogComplete(
    context: Context,
    private val stars: Int,
    private val coinsEarned: Int,
    private val onPlayAgainClick: () -> Unit,
    private val onContinueClick: () -> Unit
) : BaseDialog<DialogComplateBinding>(
    context,
    R.style.BaseDialog
) {

    override fun getLayoutDialog(): Int {
        return R.layout.dialog_complate
    }

    override fun initViews() {
        super.initViews()
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        mBinding.ivStar1.setImageResource(
            if (stars >= 1) R.drawable.ic_star_complate_on_2 else R.drawable.ic_star_complate_off_2
        )
        mBinding.ivStar2.setImageResource(
            if (stars >= 2) R.drawable.ic_star_complate_on_1 else R.drawable.ic_star_complate_off_1
        )
        mBinding.ivStar3.setImageResource(
            if (stars >= 3) R.drawable.ic_star_complate_on else R.drawable.ic_star_complate_off
        )
        mBinding.tvRewardCoins.text = coinsEarned.toString()

        mBinding.tvPlayAgain.click {
            dismiss()
            onPlayAgainClick()
        }

        mBinding.tvContinue.click {
            dismiss()
            onContinueClick()
        }
    }
}
