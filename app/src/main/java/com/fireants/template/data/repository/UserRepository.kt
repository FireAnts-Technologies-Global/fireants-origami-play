package com.fireants.template.data.repository

import com.fireants.template.data.model.player.PlayerData

interface UserRepository {
    fun getPlayer(initialHintCount: Int = 0): PlayerData
    
    fun getUnlockedPaperIds(): Set<Int>
    fun getSelectedPaperId(): Int
    
    fun getLastClaimBagTime(): Long
    fun setLastClaimBagTime(value: Long)
    
    fun getLastClaimTicketTime(): Long
    fun setLastClaimTicketTime(value: Long)

    fun addCoins(amount: Int)
    fun spendCoins(amount: Int): Boolean

    fun addStars(amount: Int)
    fun spendStars(amount: Int): Boolean

    fun addHints(amount: Int)
    fun useHint(): Boolean

    fun addTickets(amount: Int)
    fun useTicket(): Boolean

    fun selectPaper(paperId: Int)
    fun unlockPaper(paperId: Int)
    fun isPaperUnlocked(paperId: Int): Boolean

    fun claimFreeBag(now: Long): Boolean
    fun claimFreeTicket(now: Long): Boolean
}
