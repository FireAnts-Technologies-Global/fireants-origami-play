package com.fireants.template.domain.usecase.shop

import com.fireants.template.data.model.shop.ShopConfig
import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class AddRewardedCoinUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() {
        val config = ShopConfig()
        userRepository.addCoins(config.rewardedCoinAmount)
    }
}
