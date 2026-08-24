package com.fireants.template.ui.component.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.fireants.template.BuildConfig
import com.fireants.template.R
import com.fireants.template.databinding.DialogRateFeedbackBinding
import com.fireants.template.ui.bases.BaseDialog
import com.fireants.template.ui.bases.ext.click

class DialogRateFeedback(
    context: Context,
    private val rating: Float,
    private val onFeedbackSent: () -> Unit
) : BaseDialog<DialogRateFeedbackBinding>(context, R.style.BaseDialog) {

    override fun getLayoutDialog(): Int = R.layout.dialog_rate_feedback

    override fun onClickViews() {
        super.onClickViews()

        mBinding.btnCancel.click {
            dismiss()
        }

        mBinding.btnSendFeedback.click {
            val reason = mBinding.edtReason.text?.toString().orEmpty().trim()
            if (reason.isEmpty()) {
                Toast.makeText(context, R.string.rate_feedback_reason_required, Toast.LENGTH_SHORT)
                    .show()
                return@click
            }

            sendFeedback(reason)
        }
    }

    private fun sendFeedback(reason: String) {
        val subject = context.getString(
            R.string.rate_feedback_email_subject,
            context.getString(R.string.app_name)
        )
        val body = buildString {
            appendLine("Rating: ${rating.toInt()}/5")
            appendLine()
            appendLine("Reason:")
            appendLine(reason)
            appendLine()
            appendLine("App: ${context.getString(R.string.app_name)}")
            appendLine("Package: ${BuildConfig.APPLICATION_ID}")
            appendLine("Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.email_feedback))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.share_to)
                )
            )
            dismiss()
            onFeedbackSent.invoke()
        } catch (e: Exception) {
            Toast.makeText(context, R.string.no_app_available_to_open_link, Toast.LENGTH_SHORT)
                .show()
        }
    }
}
