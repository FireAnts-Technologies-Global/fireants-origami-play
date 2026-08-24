package com.pegas.origami.paper.folding.art.app

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.ProcessLifecycleOwner
import com.fireants.adsdk.admob.Admob
import com.fireants.adsdk.admob.AppOpenManager
import com.fireants.adsdk.ads.FireAntsAdSdk
import com.fireants.adsdk.application.AdsMultiDexApplication
import com.fireants.adsdk.config.AppsFlyerConfig
import com.fireants.adsdk.config.FireAntsAdSdkConfig
import com.fireants.devconfig.FireAntsDevConfig
import com.google.android.gms.ads.MobileAds
import com.pegas.origami.paper.folding.art.BuildConfig
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.ads.AdRemoteConfig
import com.pegas.origami.paper.folding.art.ads.RemoteConfigUtils
import com.pegas.origami.paper.folding.art.billing.PremiumAccessManager
import com.pegas.origami.paper.folding.art.ui.component.language.LanguageActivity
import com.pegas.origami.paper.folding.art.ui.component.onboarding.OnBoardingActivity
import com.pegas.origami.paper.folding.art.ui.component.splash.SplashActivity
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class GlobalApp : AdsMultiDexApplication() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: GlobalApp

        @SuppressLint("StaticFieldLeak")
        var currentActivity: Activity? = null
    }

    override fun onCreate() {
        super.onCreate()
        FireAntsDevConfig.init(
            context = this,
            fireantsAdsVersion = BuildConfig.FIREANTS_ADS_VERSION,
            playServicesAdsVersion = BuildConfig.PLAY_SERVICES_ADS_VERSION,
            gdprModuleVersion = BuildConfig.GDPR_MODULE_VERSION
        )
        MobileAds.initialize(this) {}

        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initAdRemoteConfig()
        initRevenueCat()
        initAds()

        // Unconditionally register lifecycle observer and callbacks so dynamic welcome/resume toggling works during testing
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
        registerActivityLifecycleCallbacks(AppActivityLifecycleCallbacks())
    }

    private fun initAdRemoteConfig() {
        AdRemoteConfig.initializeFromAssets(this)
    }

    private fun initAds() {

        val environment =
            if (BuildConfig.DEBUG) FireAntsAdSdkConfig.ENVIRONMENT_DEVELOP else FireAntsAdSdkConfig.ENVIRONMENT_PRODUCTION
        mFireAntsAdSdkConfig = FireAntsAdSdkConfig(this, environment)
        mFireAntsAdSdkConfig.listDeviceTest = listOf("E7E351334096B4438C0A70C135BDDBF2")
        val appsFlyerConfig =
            AppsFlyerConfig(true, resources.getString(R.string.appsflyer_key), BuildConfig.DEBUG)
        mFireAntsAdSdkConfig.appsFlyerConfig = appsFlyerConfig
        mFireAntsAdSdkConfig.facebookClientToken =
            resources.getString(R.string.facebook_client_token)
        applyInterstitialInterval(RemoteConfigUtils.DEFAULT_INTER_INTERVAL_SECONDS)
        FireAntsAdSdk.getInstance().init(this, mFireAntsAdSdkConfig)
        Admob.getInstance().setDisableAdResumeWhenClickAds(true)
        Admob.getInstance().setOpenActivityAfterShowInterAds(true)
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(LanguageActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(OnBoardingActivity::class.java)
        FireAntsAdSdk.getInstance().prepareLoadingAdsDialogLayout = R.layout.layout_prepare_ads
        FireAntsAdSdk.getInstance().resumeLoadingDialogLayout = R.layout.layout_welcome_back
    }

    private fun initRevenueCat() {
        Purchases.logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.INFO
        Purchases.configure(
            PurchasesConfiguration.Builder(this, BuildConfig.REVENUECAT_API_KEY).build()
        )
        PremiumAccessManager.refresh()
    }

    fun applyInterstitialInterval(intervalSeconds: Int) {
        mFireAntsAdSdkConfig.intervalInterstitialAd = intervalSeconds
    }
}
