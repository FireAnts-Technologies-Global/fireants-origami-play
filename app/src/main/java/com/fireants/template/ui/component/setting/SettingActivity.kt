package com.fireants.template.ui.component.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.fireants.adsdk.admob.AppOpenManager
import com.fireants.template.BuildConfig
import com.fireants.template.R
import com.fireants.template.app.AppConstants
import com.fireants.template.app.ResumeAdsEntryRule
import com.fireants.template.databinding.ActivitySettingBinding
import com.fireants.template.ui.bases.BaseActivity
import com.fireants.template.ui.bases.ext.click

import com.fireants.template.ui.bases.ext.showRateDialog
import com.fireants.template.ui.component.language.data.LanguageData
import com.fireants.template.utils.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    override fun getLayoutActivity(): Int = R.layout.activity_setting


    override fun initViews() {
        super.initViews()
        mBinding.toolBar.tvTitle.text = getString(R.string.setting)
        renderSettings()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.apply {
            toolBar.imgBack.click { finish() }
            switchMusic.setOnCheckedChangeListener { _, isChecked ->
                appSharedPref.isMusicEnabled = isChecked
            }
            switchSoundFx.setOnCheckedChangeListener { _, isChecked ->
                appSharedPref.isSoundFxEnabled = isChecked
            }
            rltMusic.click {
                switchMusic.isChecked = switchMusic.isChecked.not()
            }
            rltSoundFx.click {
                switchSoundFx.isChecked = switchSoundFx.isChecked.not()
            }
            rltLanguage.click {
                val bundle = Bundle()
                bundle.putBoolean(AppConstants.KEY_SETTING, true)
                Routes.startLanguageActivity(this@SettingActivity, bundle)
            }
            rltRate.click { initRate() }
            rltFeedback.click { sendFeedback(BuildConfig.email_feedback) }
            rltShare.click { shareApp(this@SettingActivity) }
            rltPolicy.click {
                openPrivacyPolicy()
            }
            rltTerms.click {
                openTermsOfUse()
            }
            rltCheckUpdate.click {
                checkForUpdate()
            }
        }
    }

    private fun renderSettings() {
        mBinding.switchMusic.isChecked = appSharedPref.isMusicEnabled
        mBinding.switchSoundFx.isChecked = appSharedPref.isSoundFxEnabled
        mBinding.tvLanguageValue.text = currentLanguageName()
        mBinding.tvVersion.text = getString(R.string.version_format, BuildConfig.VERSION_NAME)
    }

    private fun currentLanguageName(): String {
        return LanguageData.languages
            .firstOrNull { it.iso == appSharedPref.languageCode }
            ?.name
            ?: LanguageData.defaultLanguage.name
    }

    private fun initRate() {
        val isRate = appSharedPref.isRate
        if (isRate) {
            Toast.makeText(
                this@SettingActivity,
                this@SettingActivity.getString(R.string.txt_thanks_you_for_rating),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            appSharedPref.isRate = true
            showRateDialog(this@SettingActivity, false)
        }
    }

    private fun shareApp(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            var shareMessage =
                "${context.getString(R.string.app_name)}\n${context.getString(R.string.let_me_recommend)}"
            shareMessage =
                "$shareMessage\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            Handler().postDelayed({
                context.startActivity(
                    Intent.createChooser(
                        shareIntent, context.getString(R.string.share_to)
                    )
                )
            }, 250)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openPrivacyPolicy() {
        openLink(
            url = AppConstants.LINK_PRIVACY_POLICY,
            emptyMessage = getString(R.string.privacy_policy_not_configured)
        )
    }

    private fun openTermsOfUse() {
        openLink(
            url = AppConstants.LINK_TERMS_OF_USE,
            emptyMessage = getString(R.string.terms_not_configured)
        )
    }

    private fun checkForUpdate() {
        if (AppConstants.LINK_PLAY_STORE.isBlank()) {
            Toast.makeText(this, R.string.latest_version_message, Toast.LENGTH_SHORT).show()
            return
        }
        openLink(
            url = AppConstants.LINK_PLAY_STORE,
            emptyMessage = getString(R.string.latest_version_message)
        )
    }

    private fun openLink(url: String, emptyMessage: String) {
        if (url.isBlank()) {
            Toast.makeText(this, emptyMessage, Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val uri = Uri.parse(url)
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            if (browserIntent.resolveActivity(packageManager) != null) {
                startActivity(browserIntent)
                disableAdsResume()
            } else {
                Toast.makeText(this, R.string.no_app_available_to_open_link, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Log.e("SettingActivity", "Error opening link", e)
            Toast.makeText(this, R.string.unable_to_open_link, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendFeedback(email: String) {
        val intentFeedBack = Intent(Intent.ACTION_SEND)
        intentFeedBack.type = "text/email"
        intentFeedBack.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intentFeedBack.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
        intentFeedBack.putExtra(Intent.EXTRA_TEXT, "" + "")
        startActivity(Intent.createChooser(intentFeedBack, "Send Feedback:"))
    }

    override fun onResume() {
        super.onResume()

        renderSettings()
        enableAdsResume()
    }

    private fun disableAdsResume() {
        AppOpenManager.getInstance().disableAppResume()
        Log.d("hello", "Disable Ads Resume Setting")
    }

    private fun enableAdsResume() {
        if (ResumeAdsEntryRule.shouldEnableOpenResume()) {
            AppOpenManager.getInstance().enableAppResume()
            Log.d("hello", "Enable Ads Resume Setting")
        }
    }
}
