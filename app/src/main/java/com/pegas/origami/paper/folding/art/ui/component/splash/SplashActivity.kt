package com.pegas.origami.paper.folding.art.ui.component.splash

import android.annotation.SuppressLint
import android.os.CountDownTimer
import com.fireants.adsdk.admob.Admob
import com.fireants.adsdk.ads.FireAntsAdSdk
import com.fireants.adsdk.funtion.AdCallback
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.ads.AdRemoteConfig
import com.pegas.origami.paper.folding.art.ads.AdUnitConfig
import com.pegas.origami.paper.folding.art.ads.AdsManager
import com.pegas.origami.paper.folding.art.ads.AdsManager.loadNativeLanguage
import com.pegas.origami.paper.folding.art.ads.RemoteConfigUtils
import com.pegas.origami.paper.folding.art.ads.banner_splash
import com.pegas.origami.paper.folding.art.ads.inter_splash
import com.pegas.origami.paper.folding.art.app.AppConstants
import com.pegas.origami.paper.folding.art.app.GlobalApp
import com.pegas.origami.paper.folding.art.billing.PremiumAccessManager
import com.pegas.origami.paper.folding.art.databinding.ActivitySplashBinding
import com.pegas.origami.paper.folding.art.ui.bases.BannerConfig
import com.pegas.origami.paper.folding.art.ui.bases.BaseActivityWithBanner
import com.pegas.origami.paper.folding.art.ui.bases.ConsentHandler
import com.pegas.origami.paper.folding.art.ui.bases.StatusBarConfig
import com.pegas.origami.paper.folding.art.ui.bases.ext.goneView
import com.pegas.origami.paper.folding.art.ui.bases.ext.isNetwork
import com.pegas.origami.paper.folding.art.utils.Routes
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivityWithBanner<ActivitySplashBinding>(), RemoteConfigUtils.Listener {

    override var bannerConfig: BannerConfig = BannerConfig(
        adUnitConfig = AdUnitConfig(
            id = "",
            isEnable = false,
            reloadIntervalSeconds = 0
        ),
        isCollapse = false
    )

    override val statusBarConfig: StatusBarConfig
        get() = StatusBarConfig(
            applyPadding = false
        )

    private var getConfigSuccess = false
    private var isCheckingPremiumBeforeAds = false
    private var hasMovedNext = false
    private lateinit var consentHandler: ConsentHandler

    override fun getLayoutActivity() = R.layout.activity_splash

    override fun initViews() {
        super.initViews()
        appSharedPref.isRateShownInSession = false
        AdsManager.clearAll()
        RemoteConfigUtils.init(this, this)
        consentHandler = ConsentHandler(
            activity = this,
            appSharedPref = appSharedPref,
            trackingSuffix = 1,
            onConsentFlowCompleted = { loadingRemoteConfig() }
        )
        if (appSharedPref.isConfirmConsent.not() && appSharedPref.isUserGlobal.not() && isNetwork()) {
            consentHandler.requestConsent()
        } else {
            loadingRemoteConfig()
        }

    }


    private fun loadingRemoteConfig() {
        val totalTime = AppConstants.DEFAULT_TIME_SPLASH
        mBinding.progressSplash.max = 100
        mBinding.progressSplash.progress = 0
        object : CountDownTimer(totalTime, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsed = totalTime - millisUntilFinished
                mBinding.progressSplash.progress = (elapsed * 100 / totalTime).toInt()
                if (getConfigSuccess && millisUntilFinished < AppConstants.DEFAULT_LIMIT_TIME_SPLASH) {
                    checkRemoteConfigResult()
                    cancel()
                }
            }

            override fun onFinish() {
                mBinding.progressSplash.progress = 100
                if (!getConfigSuccess) {
                    checkRemoteConfigResult()
                }
            }
        }.start()
    }


    private fun checkRemoteConfigResult() {
        (application as? GlobalApp)?.applyInterstitialInterval(RemoteConfigUtils.getInterInterval())
        AdRemoteConfig.initialize(this, RemoteConfigUtils.getAdRemoteConfig())
        isCheckingPremiumBeforeAds = true
        PremiumAccessManager.refresh {
            runOnUiThread {
                isCheckingPremiumBeforeAds = false
                checkPremiumAndLoadAds()
            }
        }
    }

    private fun checkPremiumAndLoadAds() {
        if (PremiumAccessManager.isPremium(this)) {
            AdsManager.clearAll()
            mBinding.frBanner.goneView()
            moveActivity()
            return
        }

        if (AdRemoteConfig.banner_splash.isEnable) {
            bannerConfig = BannerConfig(
                AdRemoteConfig.banner_splash,
                false
            )
            loadBanner()
        } else {
            mBinding.frBanner.goneView()
        }
        loadNativeLanguage(this, R.layout.layout_native_language)

        if (AdRemoteConfig.inter_splash.isEnable && isNetwork(this@SplashActivity)) {
            Admob.getInstance().setOpenActivityAfterShowInterAds(false)
            FireAntsAdSdk.getInstance().loadSplashInterstitialAds(
                this,
                AdRemoteConfig.inter_splash.id,
                30000,
                5000,
                object : AdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        moveActivity()
                    }
                })
        } else {
            moveActivity()
        }

    }


    private fun moveActivity() {
        if (hasMovedNext) return
        hasMovedNext = true
        Routes.startLanguageActivity(this, null)
        finish()
    }


    override fun onResume() {
        super.onResume()
        if (isCheckingPremiumBeforeAds || PremiumAccessManager.isPremium(this)) return
        FireAntsAdSdk.getInstance()
            .onCheckShowSplashWhenFail(this@SplashActivity, object : AdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    moveActivity()
                }
            }, 1000)
    }

    override fun loadSuccess() {
        getConfigSuccess = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::consentHandler.isInitialized) {
            consentHandler.clear()
        }
    }
}
