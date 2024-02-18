package com.example.pokdex.data.repo

import com.example.pokdex.data.api.PokeAPI
import com.example.pokdex.domain.remote.response.Pokemon
import com.example.pokdex.domain.remote.response.PokemonList
import com.example.pokdex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokeAPI
) {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (e: Exception) {
            return Resource.Error("Something went wrong.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String) : Resource<Pokemon>{
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e: Exception) {
            return Resource.Error("Something went wrong.")
        }
        return Resource.Success(response)
    }

}