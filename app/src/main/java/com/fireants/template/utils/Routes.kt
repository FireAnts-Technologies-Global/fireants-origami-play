package com.fireants.template.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.fireants.template.app.AppConstants
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.ui.component.game.GameActivity
import com.fireants.template.ui.component.language.LanguageActivity
import com.fireants.template.ui.component.level.LevelActivity
import com.fireants.template.ui.component.main.MainActivity
import com.fireants.template.ui.component.onboarding.OnBoardingActivity
import com.fireants.template.ui.component.papercraft.PaperCraftActivity
import com.fireants.template.ui.component.papercraft.PaperCraftActivity.PaperCraftMode
import com.fireants.template.ui.component.setting.SettingActivity
import com.fireants.template.ui.component.shop.ShopActivity
import com.fireants.template.ui.component.splash.SplashActivity
import com.fireants.template.ui.component.step.StepActivity
import com.fireants.template.ui.component.welcome.WelcomeActivity

object Routes {
    fun startMainActivity(fromActivity: Activity) =
        Intent(fromActivity, MainActivity::class.java).apply {
            putExtra(AppConstants.KEY_TRACKING_SCREEN_FROM, fromActivity::class.java.simpleName)
            fromActivity.startActivity(this)
        }

    fun startGameActivity(fromActivity: Activity) =
        Intent(fromActivity, GameActivity::class.java).apply {
            putExtra(AppConstants.KEY_TRACKING_SCREEN_FROM, fromActivity::class.java.simpleName)
            fromActivity.startActivity(this)
        }

    fun startShopActivity(fromActivity: Activity) =
        Intent(fromActivity, ShopActivity::class.java).apply {
            putExtra(AppConstants.KEY_TRACKING_SCREEN_FROM, fromActivity::class.java.simpleName)
            fromActivity.startActivity(this)
        }

    fun startLevelActivity(fromActivity: Activity) =
        Intent(fromActivity, LevelActivity::class.java).apply {
            putExtra(AppConstants.KEY_TRACKING_SCREEN_FROM, fromActivity::class.java.simpleName)
            fromActivity.startActivity(this)
        }

    fun startKirigamiActivity(fromActivity: Activity) =
        startPaperCraftActivity(fromActivity, PaperCraftMode.KIRIGAMI)

    fun startOrigamiActivity(fromActivity: Activity) =
        startPaperCraftActivity(fromActivity, PaperCraftMode.ORIGAMI)

    fun startOrigami3DActivity(fromActivity: Activity) =
        startPaperCraftActivity(fromActivity, PaperCraftMode.ORIGAMI_3D)

    fun startStepActivity(fromActivity: Activity, item: ProductItem) =
        Intent(fromActivity, StepActivity::class.java).apply {
            putExtra(AppConstants.KEY_TRACKING_SCREEN_FROM, fromActivity::class.java.simpleName)
            putExtra(StepActivity.EXTRA_PRODUCT_ID, item.sourceId)
            putExtra(StepActivity.EXTRA_FAVORITE_ID, item.id)
            putExtra(StepActivity.EXTRA_GAME_TYPE, item.gameType.name)
            putExtra(StepActivity.EXTRA_PRODUCT_NAME, item.name)
            putExtra(StepActivity.EXTRA_PRODUCT_IMAGE, item.image)
            putExtra(StepActivity.EXTRA_DIFFICULTY, item.difficulty)
            putExtra(StepActivity.EXTRA_STEP_COUNT, item.stepCount)
            putExtra(StepActivity.EXTRA_ESTIMATED_TIME, item.estimatedTime)
            fromActivity.startActivity(this)
        }

    private fun startPaperCraftActivity(fromActivity: Activity, mode: PaperCraftMode) =
        Intent(fromActivity, PaperCraftActivity::class.java).apply {
            putExtra(AppConstants.KEY_TRACKING_SCREEN_FROM, fromActivity::class.java.simpleName)
            putExtra(PaperCraftActivity.EXTRA_MODE, mode.value)
            fromActivity.startActivity(this)
        }

    fun startWelcomeActivity(fromActivity: Activity) {
        if (fromActivity is WelcomeActivity) return
        val intent = Intent(fromActivity, WelcomeActivity::class.java)
        fromActivity.startActivity(intent)
    }

    fun startOnBoardingActivity(fromActivity: Activity) =
        Intent(fromActivity, OnBoardingActivity::class.java).apply {
            putExtra(AppConstants.KEY_TRACKING_SCREEN_FROM, fromActivity::class.java.simpleName)
            fromActivity.startActivity(this)
        }

    fun startLanguageActivity(fromActivity: Activity, bundle: Bundle?) =
        Intent(fromActivity, LanguageActivity::class.java).apply {
            putExtra(AppConstants.KEY_TRACKING_SCREEN_FROM, fromActivity::class.java.simpleName)
            bundle?.let { putExtras(it) }
            fromActivity.startActivity(this)
        }

    fun startSplashActivity(fromActivity: Activity) =
        Intent(fromActivity, SplashActivity::class.java).apply {
            putExtra(AppConstants.KEY_TRACKING_SCREEN_FROM, fromActivity::class.java.simpleName)
            fromActivity.startActivity(this)
        }


    fun startSettingActivity(fromActivity: Activity) =
        Intent(fromActivity, SettingActivity::class.java).apply {
            putExtra(AppConstants.KEY_TRACKING_SCREEN_FROM, fromActivity::class.java.simpleName)
            fromActivity.startActivity(this)
        }

    fun addTrackingMoveScreen(fromActivity: String, toActivity: String) {
        FireAntsTrackingHelper.fromScreenToScreen(fromActivity, toActivity)
    }

}
