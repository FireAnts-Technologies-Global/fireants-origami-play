package com.pegas.origami.paper.folding.art.ui.component.dialog

import android.content.Context
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.databinding.DialogPremiumBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseDialog
import com.pegas.origami.paper.folding.art.ui.bases.ext.click

class DialogPremium(
    context: Context,
    private val onUpgradeClick: () -> Unit
) : BaseDialog<DialogPremiumBinding>(
    context,
    R.style.BaseDialog
) {
    override fun getLayoutDialog(): Int = R.layout.dialog_premium

    override fun initViews() {
        super.initViews()
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.btnUpgrade.click {
            dismiss()
            onUpgradeClick()
        }
        mBinding.btnLater.click {
            dismiss()
        }
    }
}
