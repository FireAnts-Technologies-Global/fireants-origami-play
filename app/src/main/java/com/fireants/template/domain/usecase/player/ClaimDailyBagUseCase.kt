package com.fireants.template.domain.usecase.player

import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class ClaimDailyBagUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(now: Long = System.currentTimeMillis()): Boolean =
        repository.claimFreeBag(now)
}
