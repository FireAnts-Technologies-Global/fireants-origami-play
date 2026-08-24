package com.pegas.origami.paper.folding.art.ui.component.dialog

import android.content.Context
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.databinding.DialogRateAppBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseDialog
import com.pegas.origami.paper.folding.art.ui.bases.ext.click


class DialogRateApp(
    context: Context,
    val onRatingHighScore: () -> Unit,
    val onRatingLowScore: () -> Unit
) : BaseDialog<DialogRateAppBinding>(context, R.style.BaseDialog) {

    override fun getLayoutDialog(): Int {
        return R.layout.dialog_rate_app
    }

    override fun onClickViews() {
        super.onClickViews()

        mBinding.btnLater.click {
            dismiss()
        }

        mBinding.btnRate.click {
            val rating = mBinding.simpleRatingBar.rating
            if (rating > 0) {
                if (rating >= 4) {
                    dismiss()
                    onRatingHighScore.invoke()
                } else {
                    dismiss()
                    DialogRateFeedback(
                        context = context,
                        rating = rating,
                        onFeedbackSent = onRatingLowScore
                    ).show()
                }
            }
        }
    }
}
