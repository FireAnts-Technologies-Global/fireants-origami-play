package com.pegas.origami.paper.folding.art.ui.component.step

import androidx.lifecycle.viewModelScope
import com.pegas.origami.paper.folding.art.data.model.product.GameType
import com.pegas.origami.paper.folding.art.domain.model.step.StepGuide
import com.pegas.origami.paper.folding.art.domain.usecase.favorite.IsFavoriteUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.favorite.ToggleFavoriteByIdUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.step.GetStepGuidesUseCase
import com.pegas.origami.paper.folding.art.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StepViewModel @Inject constructor(
    private val getStepGuidesUseCase: GetStepGuidesUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleFavoriteByIdUseCase: ToggleFavoriteByIdUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(StepState())
    val state: StateFlow<StepState> = _state.asStateFlow()

    fun load(productId: Int, gameType: GameType) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    steps = getStepGuidesUseCase(productId, gameType),
                    currentIndex = 0
                )
            }
        }
    }

    fun loadFavorite(
        favoriteId: Int,
        sourceId: Int,
        gameType: GameType,
        name: String,
        image: String,
        difficulty: String,
        stepCount: Int,
        estimatedTime: String
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    favoriteId = favoriteId,
                    sourceId = sourceId,
                    gameType = gameType,
                    name = name,
                    image = image,
                    difficulty = difficulty,
                    stepCount = stepCount,
                    estimatedTime = estimatedTime,
                    isFavorite = favoriteId > 0 && isFavoriteUseCase(favoriteId)
                )
            }
        }
    }

    fun toggleFavorite() {
        val currentState = _state.value
        if (currentState.favoriteId <= 0) return

        viewModelScope.launch {
            val isFavorite = toggleFavoriteByIdUseCase(
                id = currentState.favoriteId,
                sourceId = currentState.sourceId,
                gameType = currentState.gameType.name,
                name = currentState.name,
                image = currentState.image,
                difficulty = currentState.difficulty,
                stepCount = currentState.stepCount,
                estimatedTime = currentState.estimatedTime
            )
            _state.update {
                it.copy(isFavorite = isFavorite)
            }
        }
    }

    fun previousStep() {
        _state.update {
            it.copy(currentIndex = (it.currentIndex - 1).coerceAtLeast(0))
        }
    }

    fun nextStep() {
        _state.update {
            it.copy(currentIndex = (it.currentIndex + 1).coerceAtMost((it.steps.size - 1).coerceAtLeast(0)))
        }
    }
}

data class StepState(
    val steps: List<StepGuide> = emptyList(),
    val currentIndex: Int = 0,
    val favoriteId: Int = 0,
    val sourceId: Int = 0,
    val gameType: GameType = GameType.ORIGAMI,
    val name: String = "",
    val image: String = "",
    val difficulty: String = "",
    val stepCount: Int = 0,
    val estimatedTime: String = "",
    val isFavorite: Boolean = false
) {
    val currentStep: StepGuide?
        get() = steps.getOrNull(currentIndex)
}
