package com.fireants.template.ui.component.main

import androidx.lifecycle.viewModelScope
import com.fireants.template.domain.usecase.game.GetLevelsUseCase
import com.fireants.template.domain.usecase.product.GetProductDataUseCase
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
class MainViewModel @Inject constructor(
    private val getLevelsUseCase: GetLevelsUseCase,
    private val getProductDataUseCase: GetProductDataUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<MainEvent>()
    val eventFlow: SharedFlow<MainEvent> = _eventFlow.asSharedFlow()

    init {
        loadData()
        viewModelScope.launch {
            try {
                getLevelsUseCase()
            } catch (e: Exception) {
               e.printStackTrace()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val result = getProductDataUseCase()
            if (result.isSuccess) {
                val productData = result.getOrNull()
                _state.update { 
                    it.copy(
                        productData = productData,
                        isLoading = false
                    ) 
                }
            } else {
                _state.update { it.copy(isLoading = false) }
                val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                _eventFlow.emit(MainEvent.ShowError(errorMsg))
            }
        }
    }
}