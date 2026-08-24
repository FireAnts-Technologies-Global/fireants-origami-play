package com.pegas.origami.paper.folding.art.di

import android.content.Context
import androidx.room.Room
import com.pegas.origami.paper.folding.art.data.local.db.AppDatabase
import com.pegas.origami.paper.folding.art.data.local.db.favorite.FavoriteDao
import com.pegas.origami.paper.folding.art.data.pref.AppSharedPref
import com.pegas.origami.paper.folding.art.data.pref.AppSharedPreferencesApp
import com.pegas.origami.paper.folding.art.data.repository.FavoriteRepository
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import com.pegas.origami.paper.folding.art.data.repository.KirigamiRepository
import com.pegas.origami.paper.folding.art.data.repository.Origami3DRepository
import com.pegas.origami.paper.folding.art.data.repository.OrigamiRepository
import com.pegas.origami.paper.folding.art.data.repository.ProductRepository
import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import com.pegas.origami.paper.folding.art.data.repository.impl.FavoriteRepositoryImpl
import com.pegas.origami.paper.folding.art.data.repository.impl.GameRepositoryImpl
import com.pegas.origami.paper.folding.art.data.repository.impl.KirigamiRepositoryImpl
import com.pegas.origami.paper.folding.art.data.repository.impl.Origami3DRepositoryImpl
import com.pegas.origami.paper.folding.art.data.repository.impl.OrigamiRepositoryImpl
import com.pegas.origami.paper.folding.art.data.repository.impl.ProductRepositoryImpl
import com.pegas.origami.paper.folding.art.data.repository.impl.UserRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideSharedPref(
        @ApplicationContext context: Context
    ): AppSharedPref = AppSharedPreferencesApp(context)

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "origami_play.db"
        ).build()
    }

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.favoriteDao()

    @Singleton
    @Provides
    fun provideGameRepository(impl: GameRepositoryImpl): GameRepository = impl

    @Singleton
    @Provides
    fun provideKirigamiRepository(impl: KirigamiRepositoryImpl): KirigamiRepository = impl

    @Singleton
    @Provides
    fun provideOrigami3DRepository(impl: Origami3DRepositoryImpl): Origami3DRepository = impl

    @Singleton
    @Provides
    fun provideOrigamiRepository(impl: OrigamiRepositoryImpl): OrigamiRepository = impl

    @Singleton
    @Provides
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Singleton
    @Provides
    fun provideProductRepository(impl: ProductRepositoryImpl): ProductRepository = impl

    @Singleton
    @Provides
    fun provideFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository = impl
}
