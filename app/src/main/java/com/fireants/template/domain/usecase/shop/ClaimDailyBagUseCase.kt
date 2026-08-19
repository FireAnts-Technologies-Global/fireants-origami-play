package com.fireants.template.domain.usecase.shop

import com.fireants.template.data.model.shop.ShopConfig
import com.fireants.template.data.model.shop.ShopResult
import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class ClaimDailyBagUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(nowMillis: Long): ShopResult {
        val config = ShopConfig()
        val lastClaim = userRepository.getLastClaimBagTime()
        if (nowMillis - lastClaim >= config.dailyRewardIntervalMillis) {
            userRepository.setLastClaimBagTime(nowMillis)
            return ShopResult.Success
        }
        return ShopResult.NotAvailableYet
    }
}
