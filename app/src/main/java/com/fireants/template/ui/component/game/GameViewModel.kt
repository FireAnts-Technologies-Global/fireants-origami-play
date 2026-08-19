package com.fireants.template.ui.component.game

import androidx.lifecycle.viewModelScope
import com.fireants.template.domain.usecase.game.CompleteLevelUseCase
import com.fireants.template.domain.usecase.game.GetFoldHintsUseCase
import com.fireants.template.domain.usecase.game.GetLevelUseCase
import com.fireants.template.domain.usecase.game.GetSelectedPaperUseCase
import com.fireants.template.domain.usecase.player.GetPlayerUseCase
import com.fireants.template.domain.usecase.player.UseHintUseCase
import com.fireants.template.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getLevelUseCase: GetLevelUseCase,
    private val getFoldHintsUseCase: GetFoldHintsUseCase,
    private val getSelectedPaperUseCase: GetSelectedPaperUseCase,
    private val getPlayerUseCase: GetPlayerUseCase,
    private val useHintUseCase: UseHintUseCase,
    private val completeLevelUseCase: CompleteLevelUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<GameEvent>()
    val eventFlow: SharedFlow<GameEvent> = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            getPlayerUseCase().collectLatest { player ->
                _state.update { it.copy(player = player) }
            }
        }
    }

    fun loadLevel(levelId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val level = getLevelUseCase(levelId)
                val hints = getFoldHintsUseCase(levelId)
                val paper = getSelectedPaperUseCase()
                
                _state.update { 
                    it.copy(
                        currentLevel = level,
                        foldHints = hints,
                        selectedPaper = paper,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _eventFlow.emit(GameEvent.ShowError(e.message ?: "Failed to load level"))
            }
        }
    }

    fun useHint() {
        val currentHints = _state.value.player?.hints ?: 0
        if (currentHints > 0) {
            val success = useHintUseCase()
            if (success) {
                viewModelScope.launch { _eventFlow.emit(GameEvent.ShowHint) }
            }
        } else {
            viewModelScope.launch {
                _eventFlow.emit(GameEvent.ShowError("Not enough hints!"))
            }
        }
    }

    fun completeLevel(stars: Int, moves: Int, coinReward: Int = 100) {
        val levelId = _state.value.currentLevel?.id ?: return
        viewModelScope.launch {
            try {
                completeLevelUseCase(levelId, stars, moves, coinReward)
                _eventFlow.emit(GameEvent.LevelCompleted(stars, coinReward))
            } catch (e: Exception) {
                _eventFlow.emit(GameEvent.ShowError(e.message ?: "Failed to complete level"))
            }
        }
    }
}
