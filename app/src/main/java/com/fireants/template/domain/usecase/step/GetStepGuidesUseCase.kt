package com.fireants.template.domain.usecase.step

import com.fireants.template.data.model.product.GameType
import com.fireants.template.data.repository.KirigamiRepository
import com.fireants.template.data.repository.Origami3DRepository
import com.fireants.template.data.repository.OrigamiRepository
import com.fireants.template.domain.model.step.StepGuide
import javax.inject.Inject

class GetStepGuidesUseCase @Inject constructor(
    private val origamiRepository: OrigamiRepository,
    private val kirigamiRepository: KirigamiRepository,
    private val origami3DRepository: Origami3DRepository
) {
    suspend operator fun invoke(productId: Int, gameType: GameType): List<StepGuide> {
        return when (gameType) {
            GameType.ORIGAMI -> origamiRepository.getProducts(productId).map {
                StepGuide(it.image, it.stepNumber)
            }

            GameType.KIRIGAMI -> kirigamiRepository.getProducts(productId).map {
                StepGuide(it.image, it.stepNumber)
            }

            GameType.ORIGAMI_3D -> origami3DRepository.getProducts(productId).map {
                StepGuide(it.image, it.stepNumber)
            }
        }.sortedBy { it.stepNumber }
    }
}
