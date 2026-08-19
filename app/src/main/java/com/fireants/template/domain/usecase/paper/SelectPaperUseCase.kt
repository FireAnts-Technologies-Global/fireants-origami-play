package com.fireants.template.domain.usecase.paper

import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class SelectPaperUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(paperId: Int) {
        repository.selectPaper(paperId)
    }
}
