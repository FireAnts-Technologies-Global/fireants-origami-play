package com.fireants.template.app

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
import com.fireants.template.BuildConfig
import com.fireants.template.R
import com.fireants.template.ads.AdRemoteConfig
import com.fireants.template.ads.RemoteConfigUtils
import com.fireants.template.ui.component.language.LanguageActivity
import com.fireants.template.ui.component.onboarding.OnBoardingActivity
import com.fireants.template.ui.component.splash.SplashActivity
import com.fireants.template.ui.component.uninstall.ConfirmUninstallActivity
import com.fireants.template.ui.component.uninstall.SurveyActivity
import com.google.android.gms.ads.MobileAds
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
        AppOpenManager.getInstance()
            .disableAppResumeWithActivity(ConfirmUninstallActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(SurveyActivity::class.java)
        FireAntsAdSdk.getInstance().prepareLoadingAdsDialogLayout = R.layout.layout_prepare_ads
        FireAntsAdSdk.getInstance().resumeLoadingDialogLayout = R.layout.layout_welcome_back
    }

    fun applyInterstitialInterval(intervalSeconds: Int) {
        mFireAntsAdSdkConfig.intervalInterstitialAd = intervalSeconds
    }
}