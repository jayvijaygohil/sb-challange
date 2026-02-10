package com.jayvijay.gitpeek.domain.model

data class User(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String,
    val publicRepos: Int,
)
