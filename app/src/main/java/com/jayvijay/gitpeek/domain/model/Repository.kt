package com.jayvijay.gitpeek.domain.model

data class Repository(
    val id: String,
    val name: String,
    val description: String,
    val fullName: String,
    val htmlUrl: String,
    val forksCount: Int,
    val stargazersCount: Int,
    val watchersCount: Int,
)
