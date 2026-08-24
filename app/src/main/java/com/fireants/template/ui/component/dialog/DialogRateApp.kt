package com.fireants.template.ui.component.dialog

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.fireants.template.R
import com.fireants.template.databinding.DialogRateAppBinding
import com.fireants.template.ui.bases.BaseDialog
import com.fireants.template.ui.bases.ext.click


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
                    Toast.makeText(
                        context,
                        context.getString(R.string.txt_thanks_you_for_rating),
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        dismiss()
                        onRatingLowScore.invoke()
                    }, 2000)
                }
            }
        }
    }
}