package com.pegas.origami.paper.folding.art.ui.component.main

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.pegas.origami.paper.folding.art.BuildConfig
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.ads.AdRemoteConfig
import com.pegas.origami.paper.folding.art.ads.AdsManager
import com.pegas.origami.paper.folding.art.ads.RemoteConfigUtils
import com.pegas.origami.paper.folding.art.ads.banner_all
import com.pegas.origami.paper.folding.art.data.model.ForceUpdateConfig
import com.pegas.origami.paper.folding.art.databinding.ActivityMainBinding
import com.pegas.origami.paper.folding.art.ui.bases.BannerConfig
import com.pegas.origami.paper.folding.art.ui.bases.BaseActivityWithBanner
import com.pegas.origami.paper.folding.art.ui.bases.ConsentHandler
import com.pegas.origami.paper.folding.art.ui.bases.ext.click
import com.pegas.origami.paper.folding.art.ui.component.dialog.DialogLoading
import com.pegas.origami.paper.folding.art.ui.component.main.dialog.ForceUpdateDialog
import com.pegas.origami.paper.folding.art.ui.component.main.dialog.NoInternetDialog
import com.pegas.origami.paper.folding.art.utils.ConnectionLiveData
import com.pegas.origami.paper.folding.art.utils.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivityWithBanner<ActivityMainBinding>() {

    private val viewModel: MainViewModel by viewModels()

    override val bannerConfig = BannerConfig(AdRemoteConfig.banner_all, false)
    private lateinit var consentHandler: ConsentHandler
    private val delayHandler = Handler(Looper.getMainLooper())
    private var delayRunnable: Runnable? = null
    private lateinit var noInternetDialog: NoInternetDialog
    private lateinit var forceUpdateDialog: ForceUpdateDialog
    private lateinit var dialogLoading: DialogLoading
    private var cachedForceUpdateConfig: ForceUpdateConfig? = null

    private val recommendAdapter = ProductItemAdapter(
        onItemClick = { item ->
            AdsManager.showInterHome(this) {
                Routes.startStepActivity(this, item)
            }
        },
        onFavoriteClick = { item -> viewModel.toggleFavorite(item) }
    )
    private val hotAdapter = ProductItemAdapter(
        onItemClick = { item ->
            AdsManager.showInterHome(this) {
                Routes.startStepActivity(this, item)
            }
        },
        onFavoriteClick = { item -> viewModel.toggleFavorite(item) }
    )
    private val favoriteAdapter = ProductItemAdapter(
        onItemClick = { item ->
            AdsManager.showInterHome(this) {
                Routes.startStepActivity(this, item)
            }
        },
        onFavoriteClick = { item -> viewModel.toggleFavorite(item) }
    )
    private val bannerAdapter = BannerAdapter { item ->
        AdsManager.showInterHome(this) {
            Routes.startStepActivity(this, item)
        }
    }
    private val autoSlideHandler = Handler(Looper.getMainLooper())
    private var autoSlideRunnable: Runnable? = null

    override fun getLayoutActivity(): Int = R.layout.activity_main

    override fun onResume() {
        super.onResume()
        viewModel.refreshFavoriteState()
    }

    override fun initViews() {
        super.initViews()
        AdsManager.loadInterHome(this)
        noInternetDialog = NoInternetDialog(this)
        forceUpdateDialog = ForceUpdateDialog(this)
        dialogLoading = DialogLoading(this)
        setupRecyclerViews()
        setupBannerViewPager()
        checkInternet()
        initConsentHandler()
        checkConsentStatus()
        maybeShowForceUpdateDialog()
    }

    private fun setupRecyclerViews() {
        mBinding.rvRecommend.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendAdapter
        }
        mBinding.rvHot.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = hotAdapter
        }
        mBinding.rvFavorite.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = favoriteAdapter
        }
    }

    private fun setupBannerViewPager() {
        mBinding.vpBanner.adapter = bannerAdapter
        mBinding.vpBanner.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    private fun startAutoSlide() {
        autoSlideRunnable = object : Runnable {
            override fun run() {
                val count = bannerAdapter.itemCount
                if (count > 1) {
                    val next = (mBinding.vpBanner.currentItem + 1) % count
                    mBinding.vpBanner.setCurrentItem(next, true)
                }
                autoSlideHandler.postDelayed(this, 3000L)
            }
        }
        autoSlideHandler.postDelayed(autoSlideRunnable!!, 3000L)
    }

    private fun stopAutoSlide() {
        autoSlideRunnable?.let { autoSlideHandler.removeCallbacks(it) }
        autoSlideRunnable = null
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
        dialogLoading.dismiss()
        stopAutoSlide()
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.imgStore.click {
            AdsManager.showInterHome(this) {
                Routes.startShopActivity(this)
            }
        }
        mBinding.imgSetting.click {
            Routes.startSettingActivity(this)
        }
        mBinding.btnGameMode.click {
            AdsManager.showInterHome(this) {
                Routes.startLevelActivity(this)
            }
        }
        mBinding.btnKirigami.click {
            AdsManager.showInterHome(this) {
                Routes.startKirigamiActivity(this)
            }
        }
        mBinding.btnOrigamiMode.click {
            AdsManager.showInterHome(this) {
                Routes.startOrigamiActivity(this)
            }
        }
        mBinding.btn3DOrigami.click {
            AdsManager.showInterHome(this) {
                Routes.startOrigami3DActivity(this)
            }
        }
        mBinding.tvSeeAllRecommend.click {
            AdsManager.showInterHome(this) {
                Routes.startRecommendedActivity(this)
            }
        }
        mBinding.tvSeeAllHot.click {
            AdsManager.showInterHome(this) {
                Routes.startHotActivity(this)
            }
        }
        mBinding.tvSeeAllFavorite.click {
            AdsManager.showInterHome(this) {
                Routes.startFavoriteActivity(this)
            }
        }
    }

    override fun observeData() {
        super.observeData()
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (state.isLoading) dialogLoading.show() else dialogLoading.dismiss()

                state.productData?.let { productData ->
                    recommendAdapter.submitList(productData.recommendations)
                    hotAdapter.submitList(productData.hotItems)
                    favoriteAdapter.submitList(productData.favorites)
                    val hasFavorites = productData.favorites.isNotEmpty()
                    mBinding.layoutFavoriteHeader.visibility = if (hasFavorites) View.VISIBLE else View.GONE
                    mBinding.rvFavorite.visibility = if (hasFavorites) View.VISIBLE else View.GONE

                    if (productData.banners.isNotEmpty()) {
                        bannerAdapter.submitList(productData.banners)
                        mBinding.vpBanner.visibility = View.VISIBLE
                        startAutoSlide()
                    }
                }

            }
        }

        lifecycleScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is MainEvent.ShowError -> {
                        Timber.e("MainEvent.ShowError: ${event.message}")
                    }
                }
            }
        }
    }
}
