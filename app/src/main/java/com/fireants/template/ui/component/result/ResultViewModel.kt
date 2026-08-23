package com.fireants.template.ui.component.result

import androidx.lifecycle.viewModelScope
import com.fireants.template.data.model.product.GameType
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.domain.model.product.HomeProductSection
import com.fireants.template.domain.usecase.favorite.GetFavoriteIdsUseCase
import com.fireants.template.domain.usecase.favorite.ToggleFavoriteProductUseCase
import com.fireants.template.domain.usecase.product.GetHomeProductsBySectionUseCase
import com.fireants.template.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getHomeProductsBySectionUseCase: GetHomeProductsBySectionUseCase,
    private val getFavoriteIdsUseCase: GetFavoriteIdsUseCase,
    private val toggleFavoriteProductUseCase: ToggleFavoriteProductUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state.asStateFlow()

    fun load(
        favoriteId: Int,
        sourceId: Int,
        gameType: GameType,
        name: String,
        image: String,
        difficulty: String,
        stepCount: Int,
        estimatedTime: String
    ) {
        val completedItem = ProductItem(
            id = favoriteId,
            sourceId = sourceId,
            categoryId = 0,
            name = name,
            image = image,
            order = 0,
            gameType = gameType,
            difficulty = difficulty,
            stepCount = stepCount,
            estimatedTime = estimatedTime
        )
        _state.update { it.copy(completedItem = completedItem) }
        refreshSuggestions(completedItem.id)
    }

    fun toggleFavorite(item: ProductItem) {
        viewModelScope.launch {
            val isFavorite = toggleFavoriteProductUseCase(item)
            _state.update { state ->
                state.copy(
                    suggestions = state.suggestions.map {
                        if (it.id == item.id) it.copy(isFavorite = isFavorite) else it
                    }
                )
            }
        }
    }

    private fun refreshSuggestions(completedFavoriteId: Int) {
        viewModelScope.launch {
            val favoriteIds = getFavoriteIdsUseCase()
            val suggestions = getHomeProductsBySectionUseCase(HomeProductSection.RECOMMENDED)
                .filterNot { it.id == completedFavoriteId }
                .shuffled()
                .take(6)
                .map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
            _state.update { it.copy(suggestions = suggestions) }
        }
    }
}

data class ResultState(
    val completedItem: ProductItem? = null,
    val suggestions: List<ProductItem> = emptyList()
)
