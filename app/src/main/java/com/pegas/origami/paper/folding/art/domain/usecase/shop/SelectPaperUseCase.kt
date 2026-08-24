package com.pegas.origami.paper.folding.art.domain.usecase.shop

import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import javax.inject.Inject

class SelectPaperUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(paperId: Int) {
        if (userRepository.isPaperUnlocked(paperId)) {
            userRepository.selectPaper(paperId)
        }
    }
}
