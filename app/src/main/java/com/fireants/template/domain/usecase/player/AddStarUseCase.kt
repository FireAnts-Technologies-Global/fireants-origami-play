package com.fireants.template.domain.usecase.player

import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class AddStarUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(amount: Int) {
        repository.addStars(amount)
    }
}
