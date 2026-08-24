package com.pegas.origami.paper.folding.art.domain.usecase.game

import com.pegas.origami.paper.folding.art.data.model.game.LevelProgress
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import javax.inject.Inject

class CompleteLevelUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(
        levelId: Int,
        earnedStars: Int,
        moves: Int,
        coinReward: Int
    ) {
        require(earnedStars in 0..3)
        require(coinReward >= 0)

        val currentProgress = gameRepository.getLevelProgress(levelId) ?: LevelProgress(levelId = levelId)
        
        // 1. Cập nhật số sao cao nhất & Chỉ cộng thêm số sao mới
        val newStars = maxOf(currentProgress.stars, earnedStars)
        val starsToAdd = newStars - currentProgress.stars
        if (starsToAdd > 0) {
            userRepository.addStars(starsToAdd)
        }
        
        // 2. Cộng coin thưởng
        if (coinReward > 0) {
            userRepository.addCoins(coinReward)
        }

        // 3. Lưu best moves
        val newBestMoves = if (currentProgress.bestMoves == Int.MAX_VALUE) {
            moves
        } else {
            minOf(currentProgress.bestMoves, moves)
        }

        // 4. Đánh dấu completed và lưu progress level
        val updatedProgress = currentProgress.copy(
            isCompleted = true,
            stars = newStars,
            bestMoves = newBestMoves,
            isUnlocked = true // If completed, it must be unlocked
        )
        gameRepository.saveLevelProgress(updatedProgress)

        // 5. Unlock level tiếp theo
        val levels = gameRepository.getLevels()
        val currentIndex = levels.indexOfFirst { it.id == levelId }
        if (currentIndex != -1 && currentIndex < levels.size - 1) {
            val nextLevelId = levels[currentIndex + 1].id
            val nextProgress = gameRepository.getLevelProgress(nextLevelId) ?: LevelProgress(levelId = nextLevelId)
            if (!nextProgress.isUnlocked) {
                gameRepository.saveLevelProgress(nextProgress.copy(isUnlocked = true))
            }
        }
    }
}
