package com.fireants.template.ui.component.dialog

import android.content.Context
import com.fireants.template.R
import com.fireants.template.databinding.DialogLoadingBinding
import com.fireants.template.ui.bases.BaseDialog

class DialogLoading(
    context: Context,
    private val cancelable: Boolean = false
) : BaseDialog<DialogLoadingBinding>(
    context,
    R.style.BaseDialog
) {

    override fun getLayoutDialog(): Int {
        return R.layout.dialog_loading
    }

    override fun initViews() {
        super.initViews()
        setCancelable(cancelable)
        setCanceledOnTouchOutside(cancelable)
    }
}