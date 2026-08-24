package com.pegas.origami.paper.folding.art.domain.usecase.paper

import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import javax.inject.Inject

class UnlockPaperUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(
        paperId: Int,
        cost: Int = PAPER_UNLOCK_COST
    ): Boolean {
        if (repository.isPaperUnlocked(paperId)) return true
        if (!repository.spendCoins(cost)) return false

        repository.unlockPaper(paperId)
        return true
    }

    companion object {
        const val PAPER_UNLOCK_COST = 800
    }
}
