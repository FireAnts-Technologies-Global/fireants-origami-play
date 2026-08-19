package com.fireants.template.domain.usecase.shop

import com.fireants.template.data.repository.UserRepository
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
