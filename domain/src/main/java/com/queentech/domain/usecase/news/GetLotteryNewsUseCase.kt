package com.queentech.domain.usecase.news

import com.queentech.domain.model.news.NewsArticle

interface GetLotteryNewsUseCase {
    suspend operator fun invoke(
        maxResults: Int = 20,
        query: String = "동행복권 OR 로또 OR 연금복권"
    ): Result<List<NewsArticle>>
}