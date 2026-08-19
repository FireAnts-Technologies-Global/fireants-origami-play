package com.fireants.template.domain.usecase.shop

import com.fireants.template.data.model.shop.BagStatus
import com.fireants.template.data.model.shop.ShopConfig
import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class GetBagStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(nowMillis: Long): BagStatus {
        val config = ShopConfig()
        val lastClaim = userRepository.getLastClaimBagTime()
        val elapsed = nowMillis - lastClaim
        val canClaim = elapsed >= config.dailyRewardIntervalMillis
        val remainingMillis = if (canClaim) 0L else config.dailyRewardIntervalMillis - elapsed
        return BagStatus(canClaim, remainingMillis)
    }
}
