package com.fireants.template.ui.component.level

import androidx.lifecycle.viewModelScope
import com.fireants.template.domain.usecase.game.GetLevelProgressUseCase
import com.fireants.template.domain.usecase.game.GetLevelsUseCase
import com.fireants.template.domain.usecase.player.GetPlayerUseCase
import com.fireants.template.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelViewModel @Inject constructor(
    private val getLevelsUseCase: GetLevelsUseCase,
    private val getLevelProgressUseCase: GetLevelProgressUseCase,
    private val getPlayerUseCase: GetPlayerUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(LevelState())
    val state: StateFlow<LevelState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LevelEvent>()
    val eventFlow: SharedFlow<LevelEvent> = _eventFlow.asSharedFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val player = getPlayerUseCase()
                val levels = getLevelsUseCase()
                
                val levelItems = levels.map { level ->
                    val progress = getLevelProgressUseCase(level.id)
                    LevelItemUI(level, progress)
                }
                
                _state.update { 
                    it.copy(
                        player = player,
                        levelItems = levelItems,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _eventFlow.emit(LevelEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
