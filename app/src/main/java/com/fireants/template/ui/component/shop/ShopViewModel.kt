package com.fireants.template.ui.component.shop

import androidx.lifecycle.viewModelScope
import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.data.model.player.PlayerData
import com.fireants.template.data.model.shop.BagReward
import com.fireants.template.data.model.shop.BagStatus
import com.fireants.template.data.model.shop.ShopResult
import com.fireants.template.data.model.shop.TicketStatus
import com.fireants.template.domain.usecase.player.GetPlayerUseCase
import com.fireants.template.domain.usecase.shop.*
import com.fireants.template.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getShopDataUseCase: GetShopDataUseCase,
    private val getPlayerUseCase: GetPlayerUseCase,
    private val buyPaperUseCase: BuyPaperUseCase,
    private val selectPaperUseCase: SelectPaperUseCase,
    private val buyHintUseCase: BuyHintUseCase,
    private val buyTicketUseCase: BuyTicketUseCase,
    private val buyBagUseCase: BuyBagUseCase,
    private val openBagUseCase: OpenBagUseCase,
    private val claimDailyBagUseCase: ClaimDailyBagUseCase,
    private val claimDailyTicketUseCase: ClaimDailyTicketUseCase,
    private val getBagStatusUseCase: GetBagStatusUseCase,
    private val getTicketStatusUseCase: GetTicketStatusUseCase,
    private val addRewardedCoinUseCase: AddRewardedCoinUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(ShopState())
    val state: StateFlow<ShopState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ShopEvent>()
    val eventFlow: SharedFlow<ShopEvent> = _eventFlow.asSharedFlow()

    init {
        loadData()
        
        viewModelScope.launch {
            getPlayerUseCase().collectLatest { player ->
                _state.update {
                    it.copy(player = player)
                }
                loadData()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val papers = getShopDataUseCase()
            val now = System.currentTimeMillis()
            _state.update {
                it.copy(
                    papers = papers,
                    bagStatus = getBagStatusUseCase(now),
                    ticketStatus = getTicketStatusUseCase(now)
                )
            }
        }
    }

    fun buyPaper(paperId: Int) {
        viewModelScope.launch {
            val result = buyPaperUseCase(paperId)
            if (result == ShopResult.Success) {
                loadData()
            } else {
                _eventFlow.emit(ShopEvent.ShowMessage(result))
            }
        }
    }

    fun selectPaper(paperId: Int) {
        viewModelScope.launch {
            selectPaperUseCase(paperId)
            loadData()
        }
    }

    fun buyHint(amount: Int, cost: Int) {
        viewModelScope.launch {
            val result = buyHintUseCase(amount, cost)
            if (result == ShopResult.Success) {
                loadData()
            } else {
                _eventFlow.emit(ShopEvent.ShowMessage(result))
            }
        }
    }

    fun buyTicket(amount: Int, cost: Int) {
        viewModelScope.launch {
            val result = buyTicketUseCase(amount, cost)
            if (result == ShopResult.Success) {
                loadData()
            } else {
                _eventFlow.emit(ShopEvent.ShowMessage(result))
            }
        }
    }

    fun buyBag(amount: Int, cost: Int) {
        viewModelScope.launch {
            val result = buyBagUseCase(amount, cost)
            if (result == ShopResult.Success) {
                openBags(amount)
            } else {
                _eventFlow.emit(ShopEvent.ShowMessage(result))
            }
        }
    }
    
    fun buyBagWithAd() {
        viewModelScope.launch {
            openBags(1)
        }
    }

    private fun openBags(amount: Int) {
        viewModelScope.launch {
            val rewards = mutableListOf<BagReward>()
            repeat(amount) {
                rewards.add(openBagUseCase())
            }
            loadData()
            _eventFlow.emit(ShopEvent.OnBagsOpened(rewards))
        }
    }

    fun claimDailyBag() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val result = claimDailyBagUseCase(now)
            if (result == ShopResult.Success) {
                openBags(1)
            } else {
                _eventFlow.emit(ShopEvent.ShowMessage(result))
            }
        }
    }

    fun claimDailyTicket() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val result = claimDailyTicketUseCase(now)
            if (result == ShopResult.Success) {
                loadData()
            } else {
                _eventFlow.emit(ShopEvent.ShowMessage(result))
            }
        }
    }

    fun addRewardedCoin() {
        viewModelScope.launch {
            addRewardedCoinUseCase()
            loadData()
        }
    }
}
