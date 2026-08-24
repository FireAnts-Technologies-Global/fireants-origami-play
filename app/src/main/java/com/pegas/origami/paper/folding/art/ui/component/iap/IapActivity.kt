package com.pegas.origami.paper.folding.art.ui.component.iap

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.app.AppConstants
import com.pegas.origami.paper.folding.art.databinding.ActivityIapBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseActivity
import com.pegas.origami.paper.folding.art.ui.bases.ext.click
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IapActivity : BaseActivity<ActivityIapBinding>() {
    private val viewModel: IapViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_iap
    }

    override fun initViews() {
        super.initViews()
        mBinding.benefitNoAds.tvBenefit.text = getString(R.string.iap_benefit_no_ads)
        mBinding.benefitUnlimitedHints.tvBenefit.text =
            getString(R.string.iap_benefit_unlimited_hints)
        mBinding.benefitPaperStyles.tvBenefit.text = getString(R.string.iap_benefit_paper_styles)
        underlineFooterLinks()
        setupSubscriptionSectionLink()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.imgClose.click {
            finish()
        }
        mBinding.btnContinue.click {
            Toast.makeText(this, R.string.iap_purchase_coming_soon, Toast.LENGTH_SHORT).show()
        }
        mBinding.tvRestore.click {
            Toast.makeText(this, R.string.iap_restore_coming_soon, Toast.LENGTH_SHORT).show()
        }
        mBinding.tvTerms.click {
            openLink(AppConstants.LINK_TERMS_OF_USE)
        }
    }

    private fun underlineFooterLinks() {
        mBinding.tvRestore.paintFlags = mBinding.tvRestore.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        mBinding.tvTerms.paintFlags = mBinding.tvTerms.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun setupSubscriptionSectionLink() {
        val fullText = getString(R.string.iap_terms_manage)
        val linkText = "Subscriptions section of Google Play"
        val start = fullText.indexOf(linkText)
        if (start < 0) {
            mBinding.tvManageSubscriptionNote.text = fullText
            return
        }

        val end = start + linkText.length
        val spannable = SpannableString(fullText)
        spannable.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    openGooglePlaySubscriptions()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = getColor(R.color.white)
                    ds.isUnderlineText = true
                }
            },
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.tvManageSubscriptionNote.text = spannable
        mBinding.tvManageSubscriptionNote.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvManageSubscriptionNote.highlightColor = android.graphics.Color.TRANSPARENT
    }

    private fun openGooglePlaySubscriptions() {
        val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_SUBSCRIPTIONS_DEEP_LINK))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_SUBSCRIPTIONS_WEB_LINK))
        when {
            playIntent.resolveActivity(packageManager) != null -> startActivity(playIntent)
            webIntent.resolveActivity(packageManager) != null -> startActivity(webIntent)
            else -> Toast.makeText(this, R.string.no_app_available_to_open_link, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun openLink(url: String) {
        if (url.isBlank()) {
            Toast.makeText(this, R.string.terms_not_configured, Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, R.string.no_app_available_to_open_link, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val GOOGLE_PLAY_SUBSCRIPTIONS_DEEP_LINK = "market://subscriptions"
        private const val GOOGLE_PLAY_SUBSCRIPTIONS_WEB_LINK =
            "https://play.google.com/store/account/subscriptions"
    }
}
