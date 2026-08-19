package com.fireants.template.ui.component.main

import android.os.Handler
import android.os.Looper
import com.fireants.template.BuildConfig
import com.fireants.template.R
import com.fireants.template.ads.AdRemoteConfig
import com.fireants.template.ads.RemoteConfigUtils
import com.fireants.template.ads.banner_home
import com.fireants.template.data.model.ForceUpdateConfig
import com.fireants.template.databinding.ActivityMainBinding
import com.fireants.template.ui.bases.BannerConfig
import com.fireants.template.ui.bases.BaseActivityWithBanner
import com.fireants.template.ui.bases.ConsentHandler
import com.fireants.template.ui.bases.ext.click
import com.fireants.template.ui.component.main.dialog.ForceUpdateDialog
import com.fireants.template.ui.component.main.dialog.NoInternetDialog
import com.fireants.template.utils.ConnectionLiveData
import com.fireants.template.utils.Routes
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivityWithBanner<ActivityMainBinding>() {

    override val bannerConfig = BannerConfig(AdRemoteConfig.banner_home, false)
    private lateinit var consentHandler: ConsentHandler
    private val delayHandler = Handler(Looper.getMainLooper())
    private var delayRunnable: Runnable? = null
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var forceUpdateDialog: ForceUpdateDialog
    private var cachedForceUpdateConfig: ForceUpdateConfig? = null

    override fun getLayoutActivity(): Int = R.layout.activity_main

    override fun initViews() {
        super.initViews()
        noInternetDialog = NoInternetDialog(this)
        forceUpdateDialog = ForceUpdateDialog(this)
        checkInternet()
        initConsentHandler()
        checkConsentStatus()
        maybeShowForceUpdateDialog()
    }

    private fun initConsentHandler() {
        consentHandler = ConsentHandler(
            activity = this,
            appSharedPref = appSharedPref,
            trackingSuffix = 2,
            onConsentFlowCompleted = { Timber.d("Consent flow completed") },
            onConsentSuccess = { canPersonalized ->
                if (canPersonalized) {
                    Routes.startSplashActivity(this)
                    finish()
                }
            },
            onNotUsingAdConsent = {
                appSharedPref.isUserGlobal = true
            }
        )
    }

    private fun checkConsentStatus() {
        if (appSharedPref.isConfirmConsent.not() && appSharedPref.isUserGlobal.not()) {
            delayShowConsentDialog()
        }
    }

    private fun delayShowConsentDialog() {
        if (!RemoteConfigUtils.getOnShowDialogConsent()) {
            return
        }
        delayRunnable = Runnable {
            consentHandler.requestConsent()
        }
        delayHandler.postDelayed(delayRunnable!!, 5000L)
    }

    private fun checkInternet() {
        ConnectionLiveData(this).observe(this) { isNetwork ->
            if (isNetwork) {
                Timber.d("network on")
                noInternetDialog.dismiss()
            } else {
                Timber.d("network off")
                noInternetDialog.show()
            }
        }
    }

    private fun maybeShowForceUpdateDialog() {
        val config = RemoteConfigUtils.getForceUpdateConfig() ?: return
        if (config.storeLink.isBlank()) return
        val needsUpdate = BuildConfig.VERSION_CODE < config.minVersionCode
        if (needsUpdate || config.force) {
            cachedForceUpdateConfig = config
            forceUpdateDialog.show(config)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::consentHandler.isInitialized) {
            consentHandler.clear()
        }
        delayRunnable?.let {
            delayHandler.removeCallbacks(it)
        }
        noInternetDialog.dismiss()
        forceUpdateDialog.dismiss()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.btnGameMode.click {
            Routes.startGameActivity(this)
        }

        mBinding.btnKirigami.click {
            Routes.startKirigamiActivity(this)
        }

        mBinding.btnOrigamiMode.click {
            Routes.startOrigamiActivity(this)
        }

        mBinding.btn3DOrigami.click {
            Routes.startOrigami3DActivity(this)
        }
    }
}
