package com.pegas.origami.paper.folding.art.ui.component.dialog

import android.content.Context
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.databinding.DialogComplateBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseDialog
import com.pegas.origami.paper.folding.art.ui.bases.ext.click

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
        mBinding.tvRewardCoins.text =
            mBinding.root.context.getString(R.string.number_format, coinsEarned)

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
