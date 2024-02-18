package com.example.pokdex.ui.screens.homeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokdex.R
import com.example.pokdex.domain.model.PokemonListItem

@Composable
fun HomeScreen(navController: NavController, vm: HomeViewModel = hiltViewModel()) {

    val pokemonList by vm.pokemonList.collectAsStateWithLifecycle()
    val loadError by vm.loadError.collectAsStateWithLifecycle()
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val isSearching by vm.isSearching.collectAsStateWithLifecycle()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.pokemon_logo),
                contentDescription = "pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            SearchBar(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp, top = 12.dp)
                    .fillMaxWidth()
            ) {
                vm.searchPokemon(it)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                items(
                    items = pokemonList,
                    key = { item ->
                        item.number
                    }
                ) { pokemon ->
                    PokemonItem(item = pokemon, viewModel = vm, navController = navController)
                }

                item {
                    LaunchedEffect(key1 = true) {
                        val flag = vm.flag
                        if (!flag && !isLoading && !isSearching) {
                            vm.loadPokemonListPaginated()
                        }
                        if (flag) {
                            vm.changeFlagValue()
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                if (loadError.isNotEmpty() && !isLoading) {
                    RetrySection(error = loadError) {
                        vm.loadPokemonListPaginated()
                    }
                }
            }
        }
    }

}

@Composable
fun SearchBar(
    modifier: Modifier,
    onSearch: (String) -> Unit = {}
) {

    var text by rememberSaveable {
        mutableStateOf("")
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(text)
            },
            maxLines = 1,
            singleLine = true,
            label = { Text(text = "Pokemon") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun PokemonItem(
    item: PokemonListItem,
    viewModel: HomeViewModel,
    navController: NavController
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        modifier = Modifier
            .padding(5.dp)
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(dominantColor, defaultDominantColor)
                )
            )
            .clickable {
                navController.navigate(
                    "details_screen/${dominantColor.toArgb()}/${item.pokemonName}"
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {

            AsyncImage(
                model = item.imageUrl,
                onSuccess = {
                    viewModel.calcDominantColor(it.result.drawable) { color ->
                        dominantColor = color
                    }
                },
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                placeholder = painterResource(id = R.drawable.pokemon_logo)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.pokemonName,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )

        }
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(text = error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}