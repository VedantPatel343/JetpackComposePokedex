package com.example.pokdex.domain.remote.response

data class PokemonList(
    val count: Int,
    val next: String,
    val previous: String?,
    val results: List<Result>
)