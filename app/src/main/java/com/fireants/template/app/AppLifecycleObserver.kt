package com.fireants.template.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.fireants.adsdk.admob.AppOpenManager
import com.fireants.adsdk.ads.FireAntsAdSdk
import com.fireants.adsdk.billing.AppPurchase
import com.fireants.template.ui.component.language.LanguageActivity
import com.fireants.template.ui.component.onboarding.OnBoardingActivity
import com.fireants.template.ui.component.splash.SplashActivity
import com.fireants.template.ui.component.welcome.WelcomeActivity
import com.fireants.template.utils.Routes

class AppLifecycleObserver : DefaultLifecycleObserver {

    private val listActivityDisableResume = arrayListOf(
        SplashActivity::class.java,
        LanguageActivity::class.java,
        OnBoardingActivity::class.java,
        WelcomeActivity::class.java,
    )

    override fun onStart(owner: LifecycleOwner) {
        val currentActivity = GlobalApp.currentActivity
        if (currentActivity != null) {
            val isDisable = listActivityDisableResume.any { clazz ->
                clazz.isInstance(currentActivity)
            }
            if (!isDisable && ResumeAdsEntryRule.shouldShowWelcomeOnResume() && !AppOpenManager.getInstance().isInterstitialShowing && !AppPurchase.getInstance()
                    .isPurchased(currentActivity.applicationContext) && FireAntsAdSdk.getInstance()
                    .shouldDisplayInterWelcomeBack
            ) {
                Routes.startWelcomeActivity(currentActivity)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {}
}