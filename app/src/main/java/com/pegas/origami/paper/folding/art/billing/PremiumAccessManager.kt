package com.pegas.origami.paper.folding.art.billing

import android.content.Context
import com.fireants.adsdk.billing.AppPurchase
import com.pegas.origami.paper.folding.art.BuildConfig
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getCustomerInfoWith
import java.util.concurrent.atomic.AtomicBoolean

object PremiumAccessManager {
    private val revenueCatPremium = AtomicBoolean(false)

    fun refresh() {
        if (!Purchases.isConfigured) return
        Purchases.sharedInstance.getCustomerInfoWith(
            onError = {},
            onSuccess = { customerInfo ->
                update(customerInfo)
            }
        )
    }

    fun update(customerInfo: CustomerInfo) {
        revenueCatPremium.set(isPremium(customerInfo))
    }

    fun isPremium(context: Context): Boolean {
        return revenueCatPremium.get() || AppPurchase.getInstance().isPurchased(context)
    }

    fun isPremium(customerInfo: CustomerInfo): Boolean {
        return customerInfo.entitlements[BuildConfig.REVENUECAT_ENTITLEMENT_PREMIUM]?.isActive == true
    }
}
