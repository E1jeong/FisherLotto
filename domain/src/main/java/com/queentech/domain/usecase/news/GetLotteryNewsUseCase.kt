package com.queentech.domain.usecase.news

import com.queentech.domain.model.news.NewsArticle

interface GetLotteryNewsUseCase {
    suspend operator fun invoke(
        maxResults: Int = 20,
        query: String = "로또"
    ): Result<List<NewsArticle>>
}