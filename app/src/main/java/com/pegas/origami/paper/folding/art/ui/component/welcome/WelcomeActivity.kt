package com.pegas.origami.paper.folding.art.ui.component.welcome

import com.fireants.adsdk.ads.wrapper.ApNativeAd
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.ads.AdsManager
import com.pegas.origami.paper.folding.art.ads.populateNativeAdView
import com.pegas.origami.paper.folding.art.databinding.ActivityWelcomeBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseActivity
import com.pegas.origami.paper.folding.art.ui.bases.ext.click
import com.pegas.origami.paper.folding.art.ui.bases.ext.goneView
import com.pegas.origami.paper.folding.art.ui.bases.ext.isNetwork
import com.pegas.origami.paper.folding.art.ui.bases.ext.visibleView
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
