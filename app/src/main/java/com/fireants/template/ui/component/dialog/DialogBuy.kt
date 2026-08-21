package com.fireants.template.ui.component.dialog

import android.content.Context
import com.fireants.template.R
import com.fireants.template.databinding.DialogBuyBinding
import com.fireants.template.ui.bases.BaseDialog

import com.fireants.template.ui.bases.ext.click

class DialogBuy(
    context: Context,
    private val cost: Int,
    private val currentCoins: Int,
    private val onBuyClick: () -> Unit,
    private val onAdsClick: () -> Unit
) : BaseDialog<DialogBuyBinding>(
    context,
    R.style.BaseDialog
) {

    override fun getLayoutDialog(): Int {
        return R.layout.dialog_buy
    }

    override fun initViews() {
        super.initViews()
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        mBinding.tvCost.text = cost.toString()

        if (currentCoins >= cost) {
            mBinding.tvBuy.isEnabled = true
            mBinding.tvBuy.alpha = 1.0f
        } else {
            mBinding.tvBuy.isEnabled = false
            mBinding.tvBuy.alpha = 0.5f
        }

        mBinding.tvBuy.click {
            onBuyClick()
            dismiss()
        }

        mBinding.tvAds.click {
            onAdsClick()
            dismiss()
        }
    }
}