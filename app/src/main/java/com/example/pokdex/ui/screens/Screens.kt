package com.example.pokdex.ui.screens

sealed class Screens(
    val route: String
) {

    data object HomeScreen: Screens(route = "home_screen")

    data object DetailsScreen: Screens(route = "details_screen")

    data object FavScreen: Screens(route = "favourite_screen")

}