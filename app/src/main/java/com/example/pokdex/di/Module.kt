package com.example.pokdex.di

import com.example.pokdex.data.api.PokeAPI
import com.example.pokdex.data.repo.PokemonRepository
import com.example.pokdex.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun providePokeApi(): PokeAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeAPI::class.java)
    }

    @Provides
    @Singleton
    fun providePokemonRepo(
        api: PokeAPI
    ) = PokemonRepository(api)

}