package com.pegas.origami.paper.folding.art.domain.usecase.player

import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import javax.inject.Inject

class AddStarUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(amount: Int) {
        repository.addStars(amount)
    }
}
