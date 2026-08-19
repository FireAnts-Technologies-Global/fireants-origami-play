package com.fireants.template.domain.usecase.game

import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class CompleteLevelUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(
        earnedStars: Int,
        coinReward: Int
    ) {
        require(earnedStars in 0..3)
        require(coinReward >= 0)

        repository.addStars(earnedStars)
        repository.addCoins(coinReward)
    }
}
