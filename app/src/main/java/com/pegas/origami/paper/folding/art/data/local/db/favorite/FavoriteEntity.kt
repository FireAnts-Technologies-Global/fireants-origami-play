package com.pegas.origami.paper.folding.art.data.local.db.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: Int,
    val sourceId: Int,
    val gameType: String,
    val name: String,
    val image: String,
    val difficulty: String,
    val stepCount: Int,
    val estimatedTime: String,
    val createdAt: Long = System.currentTimeMillis()
)
