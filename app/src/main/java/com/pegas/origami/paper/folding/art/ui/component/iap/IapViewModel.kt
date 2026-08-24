package com.pegas.origami.paper.folding.art.ui.component.iap

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pegas.origami.paper.folding.art.BuildConfig
import com.pegas.origami.paper.folding.art.billing.PremiumAccessManager
import com.pegas.origami.paper.folding.art.ui.bases.BaseViewModel
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

data class IapUiState(
    val isLoading: Boolean = false,
    val weeklyPrice: String? = null,
    val monthlyPrice: String? = null,
    val selectedPlan: IapPlan = IapPlan.MONTHLY,
    val isPremium: Boolean = false,
    val errorMessage: String? = null,
    val event: IapEvent? = null
)

enum class IapPlan {
    WEEKLY,
    MONTHLY
}

sealed class IapEvent {
    data object PurchaseSuccess : IapEvent()
    data object RestoreSuccess : IapEvent()
    data object NoPurchaseToRestore : IapEvent()
    data object PurchaseCancelled : IapEvent()
}

@HiltViewModel
class IapViewModel @Inject constructor() : BaseViewModel() {
    private val _state = MutableLiveData(IapUiState())
    val state: LiveData<IapUiState> = _state.toLiveData()

    private var weeklyPackage: Package? = null
    private var monthlyPackage: Package? = null

    fun load() {
        setState { copy(isLoading = true, errorMessage = null, event = null) }
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                setState { copy(isLoading = false, errorMessage = error.message) }
                refreshCustomerInfo()
            },
            onSuccess = { offerings ->
                val offering =
                    offerings[BuildConfig.REVENUECAT_OFFERING_DEFAULT] ?: offerings.current
                val packages = offering?.availablePackages.orEmpty()
                weeklyPackage = packages.findRevenueCatPackage(
                    BuildConfig.REVENUECAT_PACKAGE_WEEKLY,
                    PackageType.WEEKLY
                )
                monthlyPackage = packages.findRevenueCatPackage(
                    BuildConfig.REVENUECAT_PACKAGE_MONTHLY,
                    PackageType.MONTHLY
                )
                Timber.tag(TAG).d(
                    "Offerings loaded. requestedOffering=%s resolvedOffering=%s packages=%s weekly=%s monthly=%s",
                    BuildConfig.REVENUECAT_OFFERING_DEFAULT,
                    offering?.identifier,
                    packages.joinToString { it.debugSummary() },
                    weeklyPackage?.debugSummary(),
                    monthlyPackage?.debugSummary()
                )

                setState {
                    copy(
                        isLoading = false,
                        weeklyPrice = weeklyPackage?.product?.formattedPrice(),
                        monthlyPrice = monthlyPackage?.product?.formattedPrice(),
                        selectedPlan = when {
                            monthlyPackage != null -> IapPlan.MONTHLY
                            weeklyPackage != null -> IapPlan.WEEKLY
                            else -> selectedPlan
                        }
                    )
                }
                refreshCustomerInfo()
            }
        )
    }

    fun selectPlan(plan: IapPlan) {
        setState { copy(selectedPlan = plan, event = null) }
    }

    fun purchaseSelectedPlan(activity: Activity) {
        val selectedPackage = when (_state.value?.selectedPlan) {
            IapPlan.WEEKLY -> weeklyPackage
            IapPlan.MONTHLY, null -> monthlyPackage
        }
        if (selectedPackage == null) {
            setState { copy(errorMessage = "Package is not available yet") }
            Timber.tag(TAG).w(
                "Purchase blocked: selected package is null. selectedPlan=%s",
                _state.value?.selectedPlan
            )
            return
        }

        Timber.tag(TAG).d(
            "Starting purchase. selectedPlan=%s package=%s",
            _state.value?.selectedPlan,
            selectedPackage.debugSummary()
        )
        setState { copy(isLoading = true, errorMessage = null, event = null) }
        Purchases.sharedInstance.purchaseWith(
            purchaseParams = PurchaseParams.Builder(activity, selectedPackage).build(),
            onError = { error, userCancelled ->
                Timber.tag(TAG).e(
                    "Purchase failed. cancelled=%s code=%s message=%s underlying=%s package=%s",
                    userCancelled,
                    error.code,
                    error.message,
                    error.underlyingErrorMessage,
                    selectedPackage.debugSummary()
                )
                setState {
                    copy(
                        isLoading = false,
                        errorMessage = if (userCancelled) null else error.message,
                        event = if (userCancelled) IapEvent.PurchaseCancelled else null
                    )
                }
            },
            onSuccess = { _, customerInfo ->
                Timber.tag(TAG).d(
                    "Purchase success. premiumActive=%s activeEntitlements=%s",
                    PremiumAccessManager.isPremium(customerInfo),
                    customerInfo.entitlements.active.keys
                )
                handleCustomerInfo(customerInfo, IapEvent.PurchaseSuccess)
            }
        )
    }

    fun restorePurchases() {
        setState { copy(isLoading = true, errorMessage = null, event = null) }
        Timber.tag(TAG).d("Starting restore purchases")
        Purchases.sharedInstance.restorePurchasesWith(
            onError = { error ->
                Timber.tag(TAG).e(
                    "Restore failed. code=%s message=%s underlying=%s",
                    error.code,
                    error.message,
                    error.underlyingErrorMessage
                )
                setState { copy(isLoading = false, errorMessage = error.message) }
            },
            onSuccess = { customerInfo ->
                val event = if (PremiumAccessManager.isPremium(customerInfo)) {
                    IapEvent.RestoreSuccess
                } else {
                    IapEvent.NoPurchaseToRestore
                }
                Timber.tag(TAG).d(
                    "Restore success. premiumActive=%s activeEntitlements=%s",
                    PremiumAccessManager.isPremium(customerInfo),
                    customerInfo.entitlements.active.keys
                )
                handleCustomerInfo(customerInfo, event)
            }
        )
    }

    fun consumeEvent() {
        setState { copy(event = null, errorMessage = null) }
    }

    private fun refreshCustomerInfo() {
        Purchases.sharedInstance.getCustomerInfoWith(
            onError = {},
            onSuccess = { customerInfo ->
                PremiumAccessManager.update(customerInfo)
                setState { copy(isPremium = PremiumAccessManager.isPremium(customerInfo)) }
            }
        )
    }

    private fun handleCustomerInfo(customerInfo: CustomerInfo, event: IapEvent) {
        PremiumAccessManager.update(customerInfo)
        setState {
            copy(
                isLoading = false,
                isPremium = PremiumAccessManager.isPremium(customerInfo),
                event = event
            )
        }
    }

    private fun setState(reducer: IapUiState.() -> IapUiState) {
        _state.value = (_state.value ?: IapUiState()).reducer()
    }

    private fun List<Package>.findRevenueCatPackage(
        identifier: String,
        packageType: PackageType
    ): Package? {
        return firstOrNull { it.identifier == identifier }
            ?: firstOrNull { it.packageType == packageType }
    }

    private fun StoreProduct.formattedPrice(): String {
        return price.formatted
    }

    private fun Package.debugSummary(): String {
        val googleProduct = product as? com.revenuecat.purchases.models.GoogleStoreProduct
        return buildString {
            append("packageId=").append(identifier)
            append(", type=").append(packageType)
            append(", productId=").append(googleProduct?.productId ?: product.id)
            append(", basePlanId=").append(googleProduct?.basePlanId)
            append(", storeProductId=").append(product.id)
            append(", defaultOption=").append(product.defaultOption?.id)
            append(", price=").append(product.price.formatted)
            append(", offering=").append(product.presentedOfferingContext?.offeringIdentifier)
        }
    }

    companion object {
        private const val TAG = "RevenueCatIAP"
    }
}
