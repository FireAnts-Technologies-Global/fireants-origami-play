package com.fireants.template.ui.component.main

import androidx.lifecycle.viewModelScope
import com.fireants.template.domain.usecase.player.GetPlayerUseCase
import com.fireants.template.ui.bases.BaseViewModel
import com.fireants.template.ui.component.shop.ShopState
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
    private val getPlayerUseCase: GetPlayerUseCase
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
    }
}