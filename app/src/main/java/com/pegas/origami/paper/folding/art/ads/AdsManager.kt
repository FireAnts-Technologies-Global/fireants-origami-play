package com.pegas.origami.paper.folding.art.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.fireants.adsdk.ads.FireAntsAdSdk
import com.fireants.adsdk.ads.wrapper.ApInterstitialAd
import com.fireants.adsdk.ads.wrapper.ApNativeAd
import com.fireants.adsdk.funtion.AdCallback
import com.fireants.adsdk.util.AppConstant
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.pegas.origami.paper.folding.art.billing.PremiumAccessManager
import com.pegas.origami.paper.folding.art.ui.bases.ext.goneView
import timber.log.Timber

@SuppressLint("StaticFieldLeak")
object AdsManager {
    enum class NativeAdLoadState {
        IDLE,
        LOADING,
        LOADED,
        FAILED
    }


    val nativeLanguageAdLive = MutableLiveData<ApNativeAd?>()
    val nativeLanguageClickAdLive = MutableLiveData<ApNativeAd?>()
    val nativeLanguageAdStateLive = MutableLiveData(NativeAdLoadState.IDLE)
    val nativeLanguageClickAdStateLive = MutableLiveData(NativeAdLoadState.IDLE)
    val nativeOnboarding4AdLive = MutableLiveData<ApNativeAd?>()
    val nativeOnboardingFullAfterPage1AdLive = MutableLiveData<ApNativeAd?>()
    val nativeOnboardingFullAfterPage3AdLive = MutableLiveData<ApNativeAd?>()
    val nativeOnboarding4AdStateLive = MutableLiveData(NativeAdLoadState.IDLE)
    val nativeOnboardingFullAfterPage1AdStateLive = MutableLiveData(NativeAdLoadState.IDLE)
    val nativeOnboardingFullAfterPage3AdStateLive = MutableLiveData(NativeAdLoadState.IDLE)
    val nativeWelcomeAdLive = MutableLiveData<ApNativeAd?>()
    val nativeWelcomeAdStateLive = MutableLiveData(NativeAdLoadState.IDLE)

    private val adConfigMap = mutableMapOf<ApNativeAd, AdUnitConfig>()
    fun getAdConfig(ad: ApNativeAd): AdUnitConfig? = adConfigMap[ad]

    private var interSplashAd: ApInterstitialAd? = null
    private var interHomeAd: ApInterstitialAd? = null
    private var interWelcomeBackAd: ApInterstitialAd? = null
    private var interBackAd: ApInterstitialAd? = null
    private var interLevelAd: ApInterstitialAd? = null

    private fun loadNativeInternal(
        activity: Activity,
        config: AdUnitConfig,
        layoutRes: Int,
        liveData: MutableLiveData<ApNativeAd?>,
        shouldDisplay: Boolean = true,
        stateLiveData: MutableLiveData<NativeAdLoadState>? = null,
    ) {
        if (!config.isEnable
            || PremiumAccessManager.isPremium(activity)
            || !activity.isNetworkAvailable()
            || !shouldDisplay
        ) {
            liveData.postValue(null)
            stateLiveData?.postValue(NativeAdLoadState.FAILED)
            return
        }
        stateLiveData?.postValue(NativeAdLoadState.LOADING)
        FireAntsAdSdk.getInstance()
            .loadNativeAdResultCallback(activity, config.id, layoutRes, object : AdCallback() {
                override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
                    super.onNativeAdLoaded(nativeAd)
                    adConfigMap[nativeAd] = config
                    liveData.postValue(nativeAd)
                    stateLiveData?.postValue(NativeAdLoadState.LOADED)
                }

                override fun onAdFailedToLoad(adError: LoadAdError?) {
                    super.onAdFailedToLoad(adError)
                    liveData.postValue(null)
                    stateLiveData?.postValue(NativeAdLoadState.FAILED)
                }
            })
    }


    fun loadNativeLanguage(activity: Activity, layoutRes: Int) {
        val config =
            AdRemoteConfig.native_language
        loadNativeInternal(
            activity,
            config,
            layoutRes,
            nativeLanguageAdLive,
            stateLiveData = nativeLanguageAdStateLive
        )
    }

    fun loadNativeLanguageClick(activity: Activity, layoutRes: Int) {
        val config =
            AdRemoteConfig.native_language_click
        loadNativeInternal(
            activity,
            config,
            layoutRes,
            nativeLanguageClickAdLive,
            stateLiveData = nativeLanguageClickAdStateLive
        )
    }

    fun loadNativeOnboarding4(activity: Activity, layoutRes: Int) {
        loadNativeInternal(
            activity, AdRemoteConfig.native_onboarding_page4, layoutRes, nativeOnboarding4AdLive,
            FireAntsAdSdk.getInstance()
                .shouldDisplayNativeOnboardingNormal2,
            nativeOnboarding4AdStateLive
        )
    }

    fun loadNativeOnboardingFullAfterPage1(activity: Activity, layoutRes: Int) {
        loadNativeInternal(
            activity,
            AdRemoteConfig.native_onboarding_fullscreen12,
            layoutRes,
            nativeOnboardingFullAfterPage1AdLive,
            FireAntsAdSdk.getInstance().shouldDisplayNativeOnboardingFull1,
            nativeOnboardingFullAfterPage1AdStateLive
        )
    }

    fun loadNativeOnboardingFullAfterPage3(activity: Activity, layoutRes: Int) {
        loadNativeInternal(
            activity,
            AdRemoteConfig.native_onboarding_fullscreen23,
            layoutRes,
            nativeOnboardingFullAfterPage3AdLive,
            FireAntsAdSdk.getInstance().shouldDisplayNativeOnboardingFull2,
            nativeOnboardingFullAfterPage3AdStateLive
        )
    }


    fun loadNativeWelcome(activity: Activity, layoutRes: Int) {
        loadNativeInternal(
            activity, AdRemoteConfig.native_welcome_back, layoutRes, nativeWelcomeAdLive,
            FireAntsAdSdk.getInstance()
                .shouldDisplayNativeWelcomeBack,
            nativeWelcomeAdStateLive
        )
    }


    fun loadInterWelcome(context: Context, ignoreLimit: Boolean = false) {
        val config = AdRemoteConfig.inter_welcome_back
        if (!config.isEnable
            || PremiumAccessManager.isPremium(context)
            || (!ignoreLimit && !FireAntsAdSdk.getInstance()
                .shouldDisplayInterWelcomeBack)
        ) {
            interWelcomeBackAd = null
            return
        }
        interWelcomeBackAd =
            FireAntsAdSdk.getInstance()
                .getInterstitialAds(context, config.id, object : AdCallback() {})
    }
    fun showInterWelcome(context: Context, ignoreLimit: Boolean = false, onAction: () -> Unit) {
        val interstitial = interWelcomeBackAd
        if (interstitial != null && interstitial.isReady && !PremiumAccessManager.isPremium(context) && (ignoreLimit ||
                    FireAntsAdSdk.getInstance()
                        .shouldDisplayInterWelcomeBack)
        ) {
            FireAntsAdSdk.getInstance()
                .forceShowInterstitial(context, interstitial, object : AdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        onAction()
                    }
                }, false)
        } else {
            onAction()
        }
    }

    fun loadInterHome(context: Context) {
        val config = AdRemoteConfig.inter_home
        if (!config.isEnable || PremiumAccessManager.isPremium(context)) {
            interHomeAd = null
            return
        }
        interHomeAd = FireAntsAdSdk.getInstance()
            .getInterstitialAds(context, config.id, object : AdCallback() {})
    }

    fun showInterHome(context: Context, onAction: () -> Unit) {
        val interstitial = interHomeAd
        if (interstitial != null && interstitial.isReady && !PremiumAccessManager.isPremium(context)
        ) {
            FireAntsAdSdk.getInstance()
                .forceShowInterstitial(context, interstitial, object : AdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        onAction()
                    }
                }, true)

        } else {
            onAction()
        }
    }

    fun loadInterBack(context: Context) {
        val config = AdRemoteConfig.inter_back
        if (!config.isEnable || PremiumAccessManager.isPremium(context)) {
            interBackAd = null
            return
        }
        interBackAd = FireAntsAdSdk.getInstance()
            .getInterstitialAds(context, config.id, object : AdCallback() {})
    }

    fun showInterBack(context: Context, onAction: () -> Unit) {
        val interstitial = interBackAd
        if (interstitial != null && interstitial.isReady && !PremiumAccessManager.isPremium(context)
        ) {
            FireAntsAdSdk.getInstance()
                .forceShowInterstitial(context, interstitial, object : AdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        onAction()
                    }
                }, true)

        } else {
            onAction()
        }
    }

    fun loadInterLevel(context: Context) {
        val config = AdRemoteConfig.inter_level
        if (!config.isEnable || PremiumAccessManager.isPremium(context)) {
            interLevelAd = null
            return
        }
        interLevelAd = FireAntsAdSdk.getInstance()
            .getInterstitialAds(context, config.id, object : AdCallback() {})
    }

    fun showInterLevel(context: Context, onAction: () -> Unit) {
        val interstitial = interLevelAd
        if (interstitial != null && interstitial.isReady && !PremiumAccessManager.isPremium(context)
        ) {
            FireAntsAdSdk.getInstance()
                .forceShowInterstitial(context, interstitial, object : AdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        onAction()
                    }
                }, true)

        } else {
            onAction()
        }
    }


    fun loadBanner(
        activity: AppCompatActivity,
        adUnitConfig: AdUnitConfig,
        frAds: FrameLayout,
        isCollapse: Boolean,
    ) {
        if (adUnitConfig.isEnable && !PremiumAccessManager.isPremium(activity)) {
            removeBannerView(activity, frAds)
            if (isCollapse) FireAntsAdSdk.getInstance().loadCollapsibleBanner(
                activity,
                adUnitConfig.id,
                AppConstant.CollapsibleGravity.BOTTOM,
                object : AdCallback() {
                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        frAds.goneView()
                        Timber.tag("AdsManager_Banner")
                            .d("Load banner on ${activity.javaClass.simpleName} failed by : ${i?.message}")
                    }
                })
            else FireAntsAdSdk.getInstance()
                .loadBanner(activity, adUnitConfig.id, object : AdCallback() {
                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        frAds.goneView()
                        Timber.tag("AdsManager_Banner")
                            .d("Load banner on ${activity.javaClass.simpleName} failed by : ${i?.message}")
                    }
                })
        } else {
            frAds.removeAllViews()
            frAds.goneView()
        }
    }

    @SuppressLint("InflateParams")
    private fun removeBannerView(activity: Activity, frAds: FrameLayout) {
        try {
            val container =
                frAds.findViewById<FrameLayout>(com.fireants.adsdk.R.id.banner_container)
            if (container != null) {
                for (i in 0 until container.childCount) {
                    val view = container.getChildAt(i)
                    if (view is AdView) {
                        view.destroy()
                        container.removeView(view)
                    }
                }
            }
            val shimmerFrameLayout = LayoutInflater.from(activity)
                .inflate(com.fireants.adsdk.R.layout.layout_banner_control, null)
            frAds.removeAllViews()
            frAds.addView(shimmerFrameLayout)
        } catch (_: Exception) {
        }
    }

    fun clearAll() {
        nativeLanguageAdLive.postValue(null)
        nativeLanguageClickAdLive.postValue(null)
        nativeLanguageAdStateLive.postValue(NativeAdLoadState.IDLE)
        nativeLanguageClickAdStateLive.postValue(
            NativeAdLoadState.IDLE
        )
        nativeOnboarding4AdLive.postValue(null)
        nativeOnboardingFullAfterPage1AdLive.postValue(null)
        nativeOnboardingFullAfterPage3AdLive.postValue(null)
        nativeOnboarding4AdStateLive.postValue(NativeAdLoadState.IDLE)
        nativeOnboardingFullAfterPage1AdStateLive.postValue(
            NativeAdLoadState.IDLE
        )
        nativeOnboardingFullAfterPage3AdStateLive.postValue(
            NativeAdLoadState.IDLE
        )
        nativeWelcomeAdLive.postValue(null)
        nativeWelcomeAdStateLive.postValue(NativeAdLoadState.IDLE)
        interSplashAd = null
        interHomeAd = null
        interWelcomeBackAd = null
        interBackAd = null
        interLevelAd = null
    }

    private fun Context.isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }
}
