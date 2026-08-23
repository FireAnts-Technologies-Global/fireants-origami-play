package com.fireants.template.ui.component.step

import androidx.lifecycle.viewModelScope
import com.fireants.template.data.model.product.GameType
import com.fireants.template.domain.model.step.StepGuide
import com.fireants.template.domain.usecase.step.GetStepGuidesUseCase
import com.fireants.template.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StepViewModel @Inject constructor(
    private val getStepGuidesUseCase: GetStepGuidesUseCase
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
    val currentIndex: Int = 0
) {
    val currentStep: StepGuide?
        get() = steps.getOrNull(currentIndex)
}
