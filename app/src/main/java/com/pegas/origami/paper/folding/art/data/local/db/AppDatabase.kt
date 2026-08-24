package com.pegas.origami.paper.folding.art.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pegas.origami.paper.folding.art.data.local.db.favorite.FavoriteDao
import com.pegas.origami.paper.folding.art.data.local.db.favorite.FavoriteEntity

@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
