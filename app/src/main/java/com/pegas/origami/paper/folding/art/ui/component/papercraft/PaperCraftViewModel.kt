package com.pegas.origami.paper.folding.art.ui.component.papercraft

import androidx.lifecycle.viewModelScope
import com.pegas.origami.paper.folding.art.data.model.product.GameType
import com.pegas.origami.paper.folding.art.data.model.product.ProductItem
import com.pegas.origami.paper.folding.art.domain.model.product.HomeProductSection
import com.pegas.origami.paper.folding.art.domain.usecase.favorite.ToggleFavoriteProductUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.product.GetHomeProductsBySectionUseCase
import com.pegas.origami.paper.folding.art.domain.usecase.product.GetProductsByTypeUseCase
import com.pegas.origami.paper.folding.art.ui.bases.BaseViewModel
import com.pegas.origami.paper.folding.art.ui.component.main.ProductDisplayFormatter
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
    private val getHomeProductsBySectionUseCase: GetHomeProductsBySectionUseCase,
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
                    visibleItems = items.filterByQuery(it.query),
                    section = null
                )
            }
        }
    }

    fun load(section: HomeProductSection) {
        viewModelScope.launch {
            val items = getHomeProductsBySectionUseCase(section)
            _state.update {
                it.copy(
                    allItems = items,
                    visibleItems = items.filterByQuery(it.query),
                    section = section
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
                val allItems = if (state.section == HomeProductSection.FAVORITES && !isFavorite) {
                    state.allItems.filterNot { it.id == item.id }
                } else {
                    state.allItems.updateFavorite(item.id, isFavorite)
                }
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
    val query: String = "",
    val section: HomeProductSection? = null
)
