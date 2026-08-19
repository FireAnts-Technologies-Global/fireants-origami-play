package com.fireants.template.data.local.pref

import android.content.Context
import com.fireants.template.data.model.game.LevelProgress
import com.fireants.template.data.model.player.PlayerData
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrigamiPreference @Inject constructor(
    @ApplicationContext context: Context,
    private val moshi: Moshi
) {
    private val prefs = context.getSharedPreferences(
        PreferenceKeys.PREF_NAME,
        Context.MODE_PRIVATE
    )

    fun getPlayer(initialHintCount: Int = 0): PlayerData {
        val currentHints = if (prefs.contains(PreferenceKeys.HINTS)) {
            hints
        } else {
            initialHintCount
        }

        return PlayerData(
            coins = coins,
            stars = stars,
            hints = currentHints,
            tickets = tickets,
            selectedPaperId = selectedPaperId,
            unlockedPaperIds = unlockedPaperIds,
            lastClaimBag = lastClaimBag,
            lastClaimTicket = lastClaimTicket,
            bannerDate = prefs.getString(PreferenceKeys.BANNER_DATE, "").orEmpty(),
            bannerItems = prefs.getString(PreferenceKeys.BANNER_ITEMS, "").orEmpty()
        )
    }

    var coins: Int
        get() = prefs.getInt(PreferenceKeys.COINS, 0)
        set(value) = putInt(PreferenceKeys.COINS, value.coerceAtLeast(0))

    var stars: Int
        get() = prefs.getInt(PreferenceKeys.STARS, 0)
        set(value) = putInt(PreferenceKeys.STARS, value.coerceAtLeast(0))

    var hints: Int
        get() = prefs.getInt(PreferenceKeys.HINTS, 0)
        set(value) = putInt(PreferenceKeys.HINTS, value.coerceAtLeast(0))

    var tickets: Int
        get() = prefs.getInt(PreferenceKeys.TICKETS, 0)
        set(value) = putInt(PreferenceKeys.TICKETS, value.coerceAtLeast(0))

    var selectedPaperId: Int
        get() = prefs.getInt(PreferenceKeys.SELECTED_PAPER_ID, 1)
        set(value) = putInt(PreferenceKeys.SELECTED_PAPER_ID, value)

    var unlockedPaperIds: Set<Int>
        get() = prefs.getString(PreferenceKeys.UNLOCKED_PAPERS, "1")
            .orEmpty()
            .split(',')
            .mapNotNull(String::toIntOrNull)
            .toSet()
            .ifEmpty { setOf(1) }
        set(value) {
            prefs.edit()
                .putString(
                    PreferenceKeys.UNLOCKED_PAPERS,
                    value.sorted().joinToString(",")
                )
                .apply()
        }

    var lastClaimBag: Long
        get() = prefs.getLong(PreferenceKeys.LAST_CLAIM_BAG, 0L)
        set(value) = putLong(PreferenceKeys.LAST_CLAIM_BAG, value)

    var lastClaimTicket: Long
        get() = prefs.getLong(PreferenceKeys.LAST_CLAIM_TICKET, 0L)
        set(value) = putLong(PreferenceKeys.LAST_CLAIM_TICKET, value)

    fun setBanner(date: String, items: String) {
        prefs.edit()
            .putString(PreferenceKeys.BANNER_DATE, date)
            .putString(PreferenceKeys.BANNER_ITEMS, items)
            .apply()
    }

    private fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    private fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    private val levelProgressAdapter by lazy {
        moshi.adapter<List<LevelProgress>>(
            com.squareup.moshi.Types.newParameterizedType(List::class.java, LevelProgress::class.java)
        )
    }

    fun getLevelProgressList(): List<LevelProgress> {
        val json = prefs.getString(PreferenceKeys.LEVEL_PROGRESS, null) ?: return emptyList()
        return try {
            levelProgressAdapter.fromJson(json).orEmpty()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getLevelProgress(levelId: Int): LevelProgress? {
        return getLevelProgressList().firstOrNull { it.levelId == levelId }
    }

    fun saveLevelProgress(progress: LevelProgress) {
        val currentList = getLevelProgressList().toMutableList()
        val index = currentList.indexOfFirst { it.levelId == progress.levelId }
        if (index != -1) {
            currentList[index] = progress
        } else {
            currentList.add(progress)
        }
        saveAllLevelProgress(currentList)
    }

    fun saveAllLevelProgress(list: List<LevelProgress>) {
        val json = levelProgressAdapter.toJson(list)
        prefs.edit().putString(PreferenceKeys.LEVEL_PROGRESS, json).apply()
    }

    fun clearLevelProgress() {
        prefs.edit().remove(PreferenceKeys.LEVEL_PROGRESS).apply()
    }
}
