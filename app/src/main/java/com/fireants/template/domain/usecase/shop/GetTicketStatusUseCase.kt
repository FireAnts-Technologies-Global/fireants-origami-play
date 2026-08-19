package com.fireants.template.domain.usecase.shop

import com.fireants.template.data.model.shop.ShopConfig
import com.fireants.template.data.model.shop.TicketStatus
import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class GetTicketStatusUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(nowMillis: Long): TicketStatus {
        val config = ShopConfig()
        val lastClaim = userRepository.getLastClaimTicketTime()
        val elapsed = nowMillis - lastClaim
        val canClaim = elapsed >= config.dailyRewardIntervalMillis
        val remainingMillis = if (canClaim) 0L else config.dailyRewardIntervalMillis - elapsed
        return TicketStatus(canClaim, remainingMillis)
    }
}
