package com.pegas.origami.paper.folding.art.ui.component.level

import androidx.lifecycle.viewModelScope
import com.pegas.origami.paper.folding.art.domain.usecase.game.GetLevelProgressListUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.game.GetLevelsUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.player.GetPlayerUseCase
import com.pegas.origami.paper.folding.art.ui.bases.BaseViewModel
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
class LevelViewModel @Inject constructor(
    private val getLevelsUseCase: GetLevelsUseCase,
    private val getLevelProgressListUseCase: GetLevelProgressListUseCase,
    private val getPlayerUseCase: GetPlayerUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(LevelState())
    val state: StateFlow<LevelState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LevelEvent>()
    val eventFlow: SharedFlow<LevelEvent> = _eventFlow.asSharedFlow()

    init {
        loadData()
        
        viewModelScope.launch {
            getPlayerUseCase().collectLatest { player ->
                _state.update {
                    it.copy(player = player)
                }
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val levels = getLevelsUseCase()
                val allProgress = getLevelProgressListUseCase()
                
                val levelItems = levels.map { level ->
                    val progress = allProgress.find { it.levelId == level.id }
                    LevelItemUI(level, progress)
                }
                
                _state.update { 
                    it.copy(
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
