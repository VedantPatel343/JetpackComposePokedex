package com.example.pokdex.domain.remote.response

data class HeldItem(
    val item: Item,
    val version_details: List<VersionDetail>
)