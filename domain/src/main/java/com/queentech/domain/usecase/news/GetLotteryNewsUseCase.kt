package com.queentech.domain.usecase.news

import com.queentech.domain.model.news.NewsArticle

interface GetLotteryNewsUseCase {
    // forceRefresh가 false면 유효한 캐시를 우선 사용한다. 캐시 정책은 구현이 소유한다.
    suspend operator fun invoke(
        maxResults: Int = 20,
        query: String = "로또",
        forceRefresh: Boolean = false,
    ): Result<List<NewsArticle>>
}