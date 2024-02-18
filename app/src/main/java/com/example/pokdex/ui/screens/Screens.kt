package com.example.pokdex.ui.screens

import com.example.pokdex.util.Constants.DOMINANT_COLOR
import com.example.pokdex.util.Constants.POKEMON_NAME

sealed class Screens(
    val route: String
) {

    data object HomeScreen: Screens(route = "home_screen")

    data object DetailsScreen: Screens(route = "details_screen/{$DOMINANT_COLOR}/{$POKEMON_NAME}")

    data object FavScreen: Screens(route = "favourite_screen")

}