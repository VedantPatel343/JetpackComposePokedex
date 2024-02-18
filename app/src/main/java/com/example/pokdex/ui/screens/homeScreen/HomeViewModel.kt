package com.example.pokdex.ui.screens.homeScreen

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.pokdex.data.repo.PokemonRepository
import com.example.pokdex.domain.model.PokemonListItem
import com.example.pokdex.util.Constants.PAGE_SIZE
import com.example.pokdex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: PokemonRepository
) : ViewModel() {

    private var currentPage = 0
    var flag = true

    private val _pokemonList: MutableStateFlow<List<PokemonListItem>> =
        MutableStateFlow(emptyList())
    val pokemonList = _pokemonList.map { it }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )
    private val _loadError: MutableStateFlow<String> = MutableStateFlow("")
    val loadError = _loadError.map { it }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ""
    )
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading = _isLoading.map { it }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    private val _endReached: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val endReached = _endReached.map { it }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    private val _isSearching: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSearching = _isSearching.map { it }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )

    private var isSearchStarting = true
    private var cachedPokemons: List<PokemonListItem> = emptyList()

    init {
        loadPokemonListPaginated()
    }

    fun loadPokemonListPaginated() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repo.getPokemonList(PAGE_SIZE, currentPage * PAGE_SIZE)) {
                is Resource.Success -> {
                    _endReached.value = currentPage * PAGE_SIZE >= result.data!!.count
                    val pokedexList = result.data.results.map { item ->
                        val number = if (item.url.endsWith("/")) {
                            item.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            item.url.takeLastWhile { it.isDigit() }
                        }
                        val imageUrl =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${number}.png"
                        PokemonListItem(
                            pokemonName = item.name.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            },
                            imageUrl = imageUrl,
                            number = number.toInt()
                        )
                    }
                    currentPage++

                    _loadError.value = ""
                    _isLoading.value = false
                    _pokemonList.value += pokedexList
                }

                is Resource.Error -> {
                    _loadError.value = result.message!!
                    _isLoading.value = false
                }
            }
        }
    }

    fun searchPokemon(query: String) {
        val listToSearch = if (isSearchStarting) {
            pokemonList.value
        } else {
            cachedPokemons
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                _pokemonList.value = cachedPokemons
                _isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val result = listToSearch.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
            if (isSearchStarting) {
                cachedPokemons = pokemonList.value
                isSearchStarting = false
            }
            _pokemonList.value = result
            _isSearching.value = true
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bitmap).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { color ->
                onFinish(Color(color))
            }
        }
    }

    fun changeFlagValue() {
        flag = false
    }

}