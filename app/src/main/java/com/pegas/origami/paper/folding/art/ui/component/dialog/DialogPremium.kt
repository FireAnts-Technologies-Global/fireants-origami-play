package com.pegas.origami.paper.folding.art.ui.component.dialog

import android.content.Context
import androidx.annotation.StringRes
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.databinding.DialogPremiumBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseDialog
import com.pegas.origami.paper.folding.art.ui.bases.ext.click
import com.pegas.origami.paper.folding.art.ui.bases.ext.goneView

class DialogPremium(
    context: Context,
    @StringRes private val titleRes: Int = R.string.premium_required_title,
    @StringRes private val messageRes: Int = R.string.premium_required_message,
    @StringRes private val actionTextRes: Int = R.string.premium_upgrade,
    private val showLaterButton: Boolean = true,
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
        mBinding.tvTitle.setText(titleRes)
        mBinding.tvMessage.setText(messageRes)
        mBinding.tvUpgrade.setText(actionTextRes)
        if (!showLaterButton) {
            mBinding.btnLater.goneView()
        }
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
