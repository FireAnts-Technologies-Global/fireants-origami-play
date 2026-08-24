package com.pegas.origami.paper.folding.art.domain.usecase.paper

import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import javax.inject.Inject

class SelectPaperUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(paperId: Int) {
        repository.selectPaper(paperId)
    }
}
