package com.pegas.origami.paper.folding.art.ui.component.dialog

import android.content.Context
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.databinding.DialogLoadingBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseDialog

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