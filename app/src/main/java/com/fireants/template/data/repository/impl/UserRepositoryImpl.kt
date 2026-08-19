package com.fireants.template.data.repository.impl

import com.fireants.template.data.local.pref.OrigamiPreference
import com.fireants.template.data.model.player.PlayerData
import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val preference: OrigamiPreference
) : UserRepository {

    override fun getPlayer(initialHintCount: Int): PlayerData =
        preference.getPlayer(initialHintCount)

    override fun addCoins(amount: Int) {
        require(amount >= 0)
        preference.coins += amount
    }

    override fun spendCoins(amount: Int): Boolean {
        require(amount >= 0)
        if (preference.coins < amount) return false

        preference.coins -= amount
        return true
    }

    override fun addStars(amount: Int) {
        require(amount >= 0)
        preference.stars += amount
    }

    override fun spendStars(amount: Int): Boolean {
        require(amount >= 0)
        if (preference.stars < amount) return false

        preference.stars -= amount
        return true
    }

    override fun addHints(amount: Int) {
        require(amount >= 0)
        preference.hints += amount
    }

    override fun useHint(): Boolean {
        if (preference.hints <= 0) return false

        preference.hints -= 1
        return true
    }

    override fun addTickets(amount: Int) {
        require(amount >= 0)
        preference.tickets += amount
    }

    override fun useTicket(): Boolean {
        if (preference.tickets <= 0) return false

        preference.tickets -= 1
        return true
    }

    override fun selectPaper(paperId: Int) {
        require(isPaperUnlocked(paperId)) {
            "Paper $paperId is locked"
        }
        preference.selectedPaperId = paperId
    }

    override fun unlockPaper(paperId: Int) {
        preference.unlockedPaperIds =
            preference.unlockedPaperIds + paperId
    }

    override fun isPaperUnlocked(paperId: Int): Boolean =
        paperId in preference.unlockedPaperIds

    override fun claimFreeBag(now: Long): Boolean {
        if (now - preference.lastClaimBag < DAY_IN_MILLIS) {
            return false
        }

        preference.lastClaimBag = now
        return true
    }

    override fun claimFreeTicket(now: Long): Boolean {
        if (now - preference.lastClaimTicket < DAY_IN_MILLIS) {
            return false
        }

        preference.lastClaimTicket = now
        addTickets(1)
        return true
    }

    companion object {
        private const val DAY_IN_MILLIS = 86_400_000L
    }
}
