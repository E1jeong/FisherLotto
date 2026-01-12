package com.queentech.domain.model.news

data class NewsArticle(
    val title: String,
    val link: String,
    val source: String,
    val publishedAtEpochMillis: Long,
    val summary: String,
)