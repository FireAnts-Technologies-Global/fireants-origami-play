package com.fireants.template.ui.component.papercraft

import androidx.lifecycle.viewModelScope
import com.fireants.template.data.model.product.GameType
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.domain.usecase.favorite.ToggleFavoriteProductUseCase
import com.fireants.template.domain.usecase.product.GetProductsByTypeUseCase
import com.fireants.template.ui.bases.BaseViewModel
import com.fireants.template.ui.component.main.ProductDisplayFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaperCraftViewModel @Inject constructor(
    private val getProductsByTypeUseCase: GetProductsByTypeUseCase,
    private val toggleFavoriteProductUseCase: ToggleFavoriteProductUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(PaperCraftState())
    val state: StateFlow<PaperCraftState> = _state.asStateFlow()

    fun load(gameType: GameType) {
        viewModelScope.launch {
            val items = getProductsByTypeUseCase(gameType).sortedBy { it.order }
            _state.update {
                it.copy(
                    allItems = items,
                    visibleItems = items.filterByQuery(it.query)
                )
            }
        }
    }

    fun search(query: String) {
        _state.update {
            it.copy(
                query = query,
                visibleItems = it.allItems.filterByQuery(query)
            )
        }
    }

    fun toggleFavorite(item: ProductItem) {
        viewModelScope.launch {
            val isFavorite = toggleFavoriteProductUseCase(item)
            _state.update { state ->
                val allItems = state.allItems.updateFavorite(item.id, isFavorite)
                state.copy(
                    allItems = allItems,
                    visibleItems = allItems.filterByQuery(state.query)
                )
            }
        }
    }

    private fun List<ProductItem>.filterByQuery(query: String): List<ProductItem> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return this

        return filter { item ->
            ProductDisplayFormatter.name(item).contains(normalizedQuery, ignoreCase = true) ||
                    item.difficulty.contains(normalizedQuery, ignoreCase = true)
        }
    }

    private fun List<ProductItem>.updateFavorite(
        itemId: Int,
        isFavorite: Boolean
    ): List<ProductItem> {
        return map { item ->
            if (item.id == itemId) item.copy(isFavorite = isFavorite) else item
        }
    }
}

data class PaperCraftState(
    val allItems: List<ProductItem> = emptyList(),
    val visibleItems: List<ProductItem> = emptyList(),
    val query: String = ""
)
