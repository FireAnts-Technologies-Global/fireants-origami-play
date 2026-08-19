package com.fireants.template.ui.component.welcome

import com.fireants.adsdk.ads.wrapper.ApNativeAd
import com.fireants.template.R
import com.fireants.template.ads.AdsManager
import com.fireants.template.ads.populateNativeAdView
import com.fireants.template.databinding.ActivityWelcomeBinding
import com.fireants.template.ui.bases.BaseActivity
import com.fireants.template.ui.bases.ext.click
import com.fireants.template.ui.bases.ext.goneView
import com.fireants.template.ui.bases.ext.isNetwork
import com.fireants.template.ui.bases.ext.visibleView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {
    override fun getLayoutActivity(): Int {
        return R.layout.activity_welcome
    }

    override fun initViews() {
        super.initViews()
        showWelcomeAdLoading()
        AdsManager.loadNativeWelcome(this, R.layout.layout_native_welcome)
        AdsManager.loadInterWelcome(this)
    }

    override fun observeData() {
        super.observeData()
        AdsManager.nativeWelcomeAdStateLive.observe(this) { state ->
            when (state) {
                AdsManager.NativeAdLoadState.IDLE,
                AdsManager.NativeAdLoadState.LOADING -> showWelcomeAdLoading()

                AdsManager.NativeAdLoadState.LOADED -> Unit
                AdsManager.NativeAdLoadState.FAILED -> hideWelcomeAd()
            }
        }
        AdsManager.nativeWelcomeAdLive.observe(this) { ad ->
            if (ad != null) renderWelcomeAd(ad)
        }
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.btnStart.click {
            AdsManager.showInterWelcome(this) {
                finish()
            }
        }
    }

    private fun showWelcomeAdLoading() {
        mBinding.frAds.visibleView()
        mBinding.frAdContent.goneView()
        mBinding.shimmerAds.shimmerNativeLarge.visibleView()
        mBinding.shimmerAds.shimmerNativeLarge.startShimmer()
    }

    private fun hideWelcomeAd() {
        mBinding.shimmerAds.shimmerNativeLarge.stopShimmer()
        mBinding.frAdContent.goneView()
        mBinding.frAds.goneView()
    }

    private fun renderWelcomeAd(ad: ApNativeAd) {
        if (!isNetwork()) {
            hideWelcomeAd()
            return
        }
        mBinding.frAds.visibleView()
        mBinding.frAdContent.visibleView()
        populateNativeAdView(
            this,
            ad,
            mBinding.frAdContent,
            mBinding.shimmerAds.shimmerNativeLarge
        )
    }
}
