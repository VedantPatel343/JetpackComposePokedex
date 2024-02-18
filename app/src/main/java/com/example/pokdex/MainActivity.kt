package com.example.pokdex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokdex.ui.screens.Screens
import com.example.pokdex.ui.screens.homeScreen.HomeScreen
import com.example.pokdex.ui.screens.pokemonDetailsScreen.PokemonDetailsScreen
import com.example.pokdex.ui.theme.PokédexTheme
import com.example.pokdex.util.Constants.DOMINANT_COLOR
import com.example.pokdex.util.Constants.POKEMON_NAME
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokédexTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Pokedex()
                }
            }
        }
    }
}

@Composable
fun Pokedex() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.HomeScreen.route) {

        composable(Screens.HomeScreen.route) {
            HomeScreen(navController)
        }

        composable(
            Screens.DetailsScreen.route,
            arguments = listOf(
                navArgument(DOMINANT_COLOR) { type = NavType.IntType },
                navArgument(POKEMON_NAME) { type = NavType.StringType }
            )
        ) {
            PokemonDetailsScreen(navController = navController)
        }

    }

}