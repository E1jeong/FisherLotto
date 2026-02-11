package com.queentech.presentation.main.information

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.lotto.GetLottoNumber
import com.queentech.domain.model.news.NewsArticle
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import com.queentech.domain.usecase.news.GetLotteryNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(OrbitExperimental::class)
@HiltViewModel
class InformationViewModel @Inject constructor(
    private val getLottoNumberUseCase: GetLottoNumberUseCase,
    private val getLotteryNewsUseCase: GetLotteryNewsUseCase,
    private val prefs: SharedPreferences,
) : ViewModel(), ContainerHost<InformationState, InformationSideEffect> {

    override val container: Container<InformationState, InformationSideEffect> = container(
        initialState = InformationState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(InformationSideEffect.Toast(throwable.message ?: "Unknown Error"))
                    Log.e("!!@@", "error handler: ${throwable.message}")
                }
            }
        },
        onCreate = {
            loadLottoNumber()
        }
    )

    private fun loadLottoNumber() = blockingIntent {
        val response = getLottoNumberUseCase(drwNo = 0).getOrThrow()
        reduce { state.copy( getLottoNumberResponse = response) }
    }

    fun refreshNews() = loadNews(force = true)

    private fun loadNews(force: Boolean) = intent {
        val lastFetch = prefs.getLong(KEY_NEWS_FETCH_AT, 0L)
        val now = System.currentTimeMillis()

        // 예: 30분 캐시
        val canUseCache = !force && (now - lastFetch) < 30 * 60 * 1000L
        if (canUseCache) {
            val cachedTitles = prefs.getStringSet(KEY_NEWS_CACHE_TITLES, emptySet()).orEmpty()
            val cachedLinks = prefs.getStringSet(KEY_NEWS_CACHE_LINKS, emptySet()).orEmpty()
            // 캐시를 아주 단순하게(제목/링크)만 저장한 버전. (원하면 JSON으로 고도화 가능)
            if (cachedTitles.isNotEmpty() && cachedLinks.isNotEmpty()) {
                // 제목/링크 개수 mismatch 방지
                val pairs = cachedTitles.zip(cachedLinks)
                val cached = pairs.map { (t, l) ->
                    NewsArticle(
                        title = t,
                        link = l,
                        source = "",
                        publishedAtEpochMillis = 0L,
                        summary = "",
                    )
                }
                reduce { state.copy(news = cached, isNewsLoading = false) }
                return@intent
            }
        }

        reduce { state.copy(isNewsLoading = true) }

        val news = getLotteryNewsUseCase(maxResults = 20).getOrThrow()

        // 캐시(간단 버전)
        prefs.edit().apply {
            putLong(KEY_NEWS_FETCH_AT, now)
            putStringSet(KEY_NEWS_CACHE_TITLES, news.map { it.title }.toSet())
            putStringSet(KEY_NEWS_CACHE_LINKS, news.map { it.link }.toSet())
        }.apply()

        reduce { state.copy(news = news, isNewsLoading = false) }
    }

    companion object {
        private const val KEY_NEWS_FETCH_AT = "news_fetch_at"
        private const val KEY_NEWS_CACHE_TITLES = "news_cache_titles"
        private const val KEY_NEWS_CACHE_LINKS = "news_cache_links"
    }
}


@Immutable
data class InformationState(
    val getLottoNumberResponse: GetLottoNumber = GetLottoNumber(
        firstCount = "0",
        firstMoney = "0",
        secondCount = "0",
        secondMoney = "0",
        thirdCount = "0",
        thirdMoney = "0",
        fourthCount = "0",
        fourthMoney = "0",
        fifthCount = "0",
        fifthMoney = "0",
        bonus = "0",
        num1 = "00",
        num2 = "00",
        num3 = "00",
        num4 = "00",
        num5 = "00",
        num6 = "00",
        pdate = "",
        round = "0",
    ),

    val news: List<NewsArticle> = emptyList(),
    val isNewsLoading: Boolean = false,
)

sealed interface InformationSideEffect {
    class Toast(val message: String) : InformationSideEffect
}