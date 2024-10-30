package com.example.project1.model

import retrofit2.http.GET

data class Pokemon(
    val id: Int,
    val num: String,
    val name: String,
    val img: String,
    val type: List<String>,
    val height: String,
    val weight: String,

    val candy: String,
    val candy_count: Int,
    val egg: String,
    val spawn_chance: Double,
    val avg_spawns: Double,
    val spawn_time: String,
    val multipliers: List<Double?>?,
    val weaknesses: List<String>,
    val prev_evolution: List<MiniPokemon>?,
    val next_evolution: List<MiniPokemon>?
)

data class MiniPokemon(
    val num: String,
    val name: String
)

data class PokemonResponse(
    val pokemon: List<Pokemon> // Aquí está el cambio: ahora es una lista
)

interface PokemonApi {
    @GET("pokedex.json")
    suspend fun getPokemon(): PokemonResponse
}