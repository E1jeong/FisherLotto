package com.queentech.presentation.main.information

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.lotto.GetLottoNumber
import com.queentech.domain.model.news.NewsArticle
import com.queentech.domain.usecase.lotto.GetLatestDrawNumberUseCase
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
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@OptIn(OrbitExperimental::class)
@HiltViewModel
class InformationViewModel @Inject constructor(
    private val getLottoNumberUseCase: GetLottoNumberUseCase,
    private val getLatestDrawNumberUseCase: GetLatestDrawNumberUseCase,
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
        val cachedDate = prefs.getString(KEY_DRW_DATE, null)
        val cachedDrawNo = prefs.getInt(KEY_DRAW_NUMBER, 0)

        val useCache = if (cachedDate != null && cachedDrawNo != 0) {
            try {
                val lastDate = LocalDate.parse(cachedDate)
                val days = ChronoUnit.DAYS.between(lastDate, LocalDate.now())
                days < 7
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }

        if (useCache) {
            val cached = GetLottoNumber(
                eachWinnings = prefs.getLong(KEY_EACH_WINNINGS, 0L),
                winnerCount = prefs.getInt(KEY_WINNER_COUNT, 0),
                totalWinnings = prefs.getLong(KEY_TOTAL_WINNINGS, 0L),
                drwNo = cachedDrawNo,
                drwNoDate = cachedDate!!,
                drwtNo1 = prefs.getInt(KEY_NO1, 0),
                drwtNo2 = prefs.getInt(KEY_NO2, 0),
                drwtNo3 = prefs.getInt(KEY_NO3, 0),
                drwtNo4 = prefs.getInt(KEY_NO4, 0),
                drwtNo5 = prefs.getInt(KEY_NO5, 0),
                drwtNo6 = prefs.getInt(KEY_NO6, 0),
                bnusNo = prefs.getInt(KEY_BONUS, 0),
            )

            reduce { state.copy(latestDrawNumber = cachedDrawNo, getLottoNumberResponse = cached) }
            return@blockingIntent
        }

        val latestDraw = getLatestDrawNumberUseCase().getOrThrow()
        val response = getLottoNumberUseCase(drwNo = latestDraw).getOrThrow()
        Log.d("!!@@", "loadLottoNumber: $response")

        prefs.edit().apply {
            putInt(KEY_DRAW_NUMBER, latestDraw)
            putString(KEY_DRW_DATE, response.drwNoDate)
            putInt(KEY_NO1, response.drwtNo1)
            putInt(KEY_NO2, response.drwtNo2)
            putInt(KEY_NO3, response.drwtNo3)
            putInt(KEY_NO4, response.drwtNo4)
            putInt(KEY_NO5, response.drwtNo5)
            putInt(KEY_NO6, response.drwtNo6)
            putInt(KEY_BONUS, response.bnusNo)
            putInt(KEY_WINNER_COUNT, response.winnerCount)
            putLong(KEY_EACH_WINNINGS, response.eachWinnings)
            putLong(KEY_TOTAL_WINNINGS, response.totalWinnings)
        }.apply()

        reduce { state.copy(latestDrawNumber = latestDraw, getLottoNumberResponse = response) }
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
        private const val KEY_DRAW_NUMBER = "latest_draw_number"
        private const val KEY_DRW_DATE = "drw_no_date"
        private const val KEY_NO1 = "drwt_no1"
        private const val KEY_NO2 = "drwt_no2"
        private const val KEY_NO3 = "drwt_no3"
        private const val KEY_NO4 = "drwt_no4"
        private const val KEY_NO5 = "drwt_no5"
        private const val KEY_NO6 = "drwt_no6"
        private const val KEY_BONUS = "bnus_no"
        private const val KEY_WINNER_COUNT = "winner_count"
        private const val KEY_EACH_WINNINGS = "each_winnings"
        private const val KEY_TOTAL_WINNINGS = "total_winnings"

        private const val KEY_NEWS_FETCH_AT = "news_fetch_at"
        private const val KEY_NEWS_CACHE_TITLES = "news_cache_titles"
        private const val KEY_NEWS_CACHE_LINKS = "news_cache_links"
    }
}


@Immutable
data class InformationState(
    val getLottoNumberResponse: GetLottoNumber = GetLottoNumber(
        eachWinnings = 0,
        winnerCount = 0,
        totalWinnings = 0,
        drwNo = 0,
        drwNoDate = "",
        drwtNo1 = 0,
        drwtNo2 = 0,
        drwtNo3 = 0,
        drwtNo4 = 0,
        drwtNo5 = 0,
        drwtNo6 = 0,
        bnusNo = 0,
    ),
    val latestDrawNumber: Int = 0,

    val news: List<NewsArticle> = emptyList(),
    val isNewsLoading: Boolean = false,
)

sealed interface InformationSideEffect {
    class Toast(val message: String) : InformationSideEffect
}