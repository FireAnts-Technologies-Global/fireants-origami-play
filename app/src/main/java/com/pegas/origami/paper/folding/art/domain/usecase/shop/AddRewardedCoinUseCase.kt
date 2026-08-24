package com.pegas.origami.paper.folding.art.domain.usecase.shop

import com.pegas.origami.paper.folding.art.data.model.shop.ShopConfig
import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import javax.inject.Inject

class AddRewardedCoinUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() {
        val config = ShopConfig()
        userRepository.addCoins(config.rewardedCoinAmount)
    }
}
