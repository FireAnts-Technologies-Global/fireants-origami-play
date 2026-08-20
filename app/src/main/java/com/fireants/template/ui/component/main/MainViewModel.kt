package com.fireants.template.ui.component.main

import androidx.lifecycle.viewModelScope
import com.fireants.template.domain.usecase.game.GetLevelsUseCase
import com.fireants.template.domain.usecase.player.GetPlayerUseCase
import com.fireants.template.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPlayerUseCase: GetPlayerUseCase,
    private val getLevelsUseCase: GetLevelsUseCase
) : BaseViewModel(

){

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getPlayerUseCase().collectLatest { player ->
                _state.update {
                    it.copy(player = player)
                }
            }
        }

        viewModelScope.launch {
            try {
                // Preload to cache
                getLevelsUseCase()
            } catch (e: Exception) {
                // Ignore preload errors
            }
        }
    }
}