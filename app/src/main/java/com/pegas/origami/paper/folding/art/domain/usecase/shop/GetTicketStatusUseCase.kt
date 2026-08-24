package com.pegas.origami.paper.folding.art.domain.usecase.shop

import com.pegas.origami.paper.folding.art.data.model.shop.ShopConfig
import com.pegas.origami.paper.folding.art.data.model.shop.TicketStatus
import com.pegas.origami.paper.folding.art.data.repository.UserRepository
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
