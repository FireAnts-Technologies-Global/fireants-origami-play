package com.fireants.template.di

import android.content.Context
import com.fireants.template.data.pref.AppSharedPref
import com.fireants.template.data.pref.AppSharedPreferencesApp
import com.fireants.template.data.repository.GameRepository
import com.fireants.template.data.repository.KirigamiRepository
import com.fireants.template.data.repository.Origami3DRepository
import com.fireants.template.data.repository.OrigamiRepository
import com.fireants.template.data.repository.ProductRepository
import com.fireants.template.data.repository.UserRepository
import com.fireants.template.data.repository.impl.GameRepositoryImpl
import com.fireants.template.data.repository.impl.KirigamiRepositoryImpl
import com.fireants.template.data.repository.impl.Origami3DRepositoryImpl
import com.fireants.template.data.repository.impl.OrigamiRepositoryImpl
import com.fireants.template.data.repository.impl.ProductRepositoryImpl
import com.fireants.template.data.repository.impl.UserRepositoryImpl
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
}
