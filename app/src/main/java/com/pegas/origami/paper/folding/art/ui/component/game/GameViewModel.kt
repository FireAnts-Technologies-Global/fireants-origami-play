package com.pegas.origami.paper.folding.art.ui.component.game

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.pegas.origami.paper.folding.art.billing.PremiumAccessManager
import com.pegas.origami.paper.folding.art.domain.usecase.game.CompleteLevelUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.game.GetFoldHintsUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.game.GetLevelUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.game.GetLevelsUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.game.GetSelectedPaperUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.player.GetPlayerUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.player.UseHintUseCase
import com.pegas.origami.paper.folding.art.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val getLevelUseCase: GetLevelUseCase,
    private val getLevelsUseCase: GetLevelsUseCase,
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
                val levels = getLevelsUseCase()
                val hints = getFoldHintsUseCase(levelId)
                val paper = getSelectedPaperUseCase()
                val currentIndex = levels.indexOfFirst { it.id == levelId }
                val nextLevelId = if (currentIndex != -1) {
                    levels.getOrNull(currentIndex + 1)?.id
                } else {
                    null
                }
                
                _state.update { 
                    it.copy(
                        currentLevel = level,
                        nextLevelId = nextLevelId,
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
        if (currentHints > 0 || PremiumAccessManager.isPremium(context)) {
            val success = useHintUseCase()
            if (success) {
                viewModelScope.launch { _eventFlow.emit(GameEvent.ShowHint) }
            }
        } else {
            viewModelScope.launch {
                _eventFlow.emit(GameEvent.OpenStore)
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
