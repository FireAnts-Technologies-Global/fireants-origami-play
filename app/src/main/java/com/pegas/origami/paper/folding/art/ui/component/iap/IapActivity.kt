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
import com.pegas.origami.paper.folding.art.ui.bases.ext.goneView
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
        mBinding.planYearly.goneView()
        underlineFooterLinks()
        setupSubscriptionSectionLink()
        viewModel.load()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.imgClose.click {
            finish()
        }
        mBinding.planWeekly.click {
            viewModel.selectPlan(IapPlan.WEEKLY)
        }
        mBinding.planMonthly.click {
            viewModel.selectPlan(IapPlan.MONTHLY)
        }
        mBinding.btnContinue.click {
            if (viewModel.state.value?.isPremium == true) {
                openGooglePlaySubscriptions()
            } else {
                viewModel.purchaseSelectedPlan(this)
            }
        }
        mBinding.tvRestore.click {
            viewModel.restorePurchases()
        }
        mBinding.tvTerms.click {
            openLink(AppConstants.LINK_TERMS_OF_USE)
        }
    }

    override fun observeData() {
        super.observeData()
        viewModel.state.observe(this) { state ->
            if (state.isLoading) showLoading() else hideLoading()
            mBinding.tvWeeklyPrice.text = state.weeklyPrice ?: getString(R.string.iap_price_loading)
            mBinding.tvMonthlyPrice.text =
                state.monthlyPrice ?: getString(R.string.iap_price_loading)
            renderSelectedPlan(state.selectedPlan)
            renderSubscriptionState(state)
            renderPlanNote(state)
            state.errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.consumeEvent()
            }
            when (state.event) {
                IapEvent.PurchaseSuccess -> {
                    Toast.makeText(this, R.string.iap_purchase_success, Toast.LENGTH_SHORT).show()
                    viewModel.consumeEvent()
                    finish()
                }

                IapEvent.RestoreSuccess -> {
                    Toast.makeText(this, R.string.iap_restore_success, Toast.LENGTH_SHORT).show()
                    viewModel.consumeEvent()
                    finish()
                }

                IapEvent.NoPurchaseToRestore -> {
                    Toast.makeText(this, R.string.iap_restore_empty, Toast.LENGTH_SHORT).show()
                    viewModel.consumeEvent()
                }

                IapEvent.PurchaseCancelled -> {
                    viewModel.consumeEvent()
                }

                null -> Unit
            }
        }
    }

    private fun renderSelectedPlan(selectedPlan: IapPlan) {
        mBinding.planWeekly.setBackgroundResource(
            if (selectedPlan == IapPlan.WEEKLY) R.drawable.bg_iap_plan_selected else R.drawable.bg_iap_plan
        )
        mBinding.planMonthly.setBackgroundResource(
            if (selectedPlan == IapPlan.MONTHLY) R.drawable.bg_iap_plan_selected else R.drawable.bg_iap_plan
        )
    }

    private fun renderSubscriptionState(state: IapUiState) {
        mBinding.tvWeeklySubtitle.text =
            if (state.activePlan == IapPlan.WEEKLY) getString(R.string.iap_current_plan)
            else getString(R.string.iap_weekly_subtitle)
        mBinding.tvMonthlySubtitle.text =
            if (state.activePlan == IapPlan.MONTHLY) getString(R.string.iap_current_plan)
            else getString(R.string.iap_monthly_subtitle)
        mBinding.btnContinue.text = when {
            state.isPremium -> getString(R.string.iap_manage_subscription)
            state.selectedPlan == IapPlan.MONTHLY -> getString(R.string.iap_start_free_trial)
            else -> getString(R.string.iap_subscribe_weekly)
        }
    }

    private fun renderPlanNote(state: IapUiState) {
        if (state.isPremium) {
            mBinding.tvPlanNote.visibility = View.GONE
            return
        }

        mBinding.tvPlanNote.visibility = View.VISIBLE
        mBinding.tvPlanNote.text = when (state.selectedPlan) {
            IapPlan.WEEKLY -> state.weeklyPrice?.let {
                getString(R.string.iap_plan_note_weekly, it)
            }

            IapPlan.MONTHLY -> state.monthlyPrice?.let {
                getString(R.string.iap_plan_note_monthly_trial, it)
            }
        } ?: getString(R.string.iap_plan_note_loading)
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
