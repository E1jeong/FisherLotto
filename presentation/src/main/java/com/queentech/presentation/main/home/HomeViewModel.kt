package com.queentech.presentation.main.home

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
class HomeViewModel @Inject constructor(
    private val getLottoNumberUseCase: GetLottoNumberUseCase,
    private val getLotteryNewsUseCase: GetLotteryNewsUseCase,
    private val prefs: SharedPreferences,
) : ViewModel(), ContainerHost<HomeState, HomeSideEffect> {

    override val container: Container<HomeState, HomeSideEffect> = container(
        initialState = HomeState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(HomeSideEffect.Toast(throwable.message ?: "Unknown Error"))
                    Log.e("!!@@", "error handler: ${throwable.message}")
                }
            }
        },
        onCreate = {
            loadLottoNumber()
            loadNews(force = false)
        }
    )

    private fun loadLottoNumber() = blockingIntent {
        val response = getLottoNumberUseCase(round = 0).getOrThrow()
        reduce { state.copy( getLottoNumberResponse = response) }
    }

    fun refreshNews() = loadNews(force = true)

    private fun loadNews(force: Boolean) = intent {
        val lastFetch = prefs.getLong(KEY_NEWS_FETCH_AT, 0L)
        val now = System.currentTimeMillis()

        // 예: 30분 캐시
        val canUseCache = !force && (now - lastFetch) < 30 * 60 * 1000L
        if (canUseCache) {
            val cachedJson = prefs.getString(KEY_NEWS_CACHE, null)
            if (!cachedJson.isNullOrEmpty()) {
                val type = object : TypeToken<List<NewsArticle>>() {}.type
                val cached = gson.fromJson<List<NewsArticle>>(cachedJson, type)
                if (cached.isNotEmpty()) {
                    reduce { state.copy(news = cached, isNewsLoading = false) }
                    return@intent
                }
            }
        }

        reduce { state.copy(isNewsLoading = true) }

        getLotteryNewsUseCase(maxResults = 20)
            .onSuccess { news ->
                prefs.edit {
                    putLong(KEY_NEWS_FETCH_AT, now)
                    putString(KEY_NEWS_CACHE, gson.toJson(news))
                }
                reduce { state.copy(news = news, isNewsLoading = false) }
            }
            .onFailure { e ->
                Log.e(TAG, "뉴스 로딩 실패", e)
                reduce { state.copy(isNewsLoading = false) }
                postSideEffect(HomeSideEffect.Toast("뉴스를 불러오지 못했습니다."))
            }
    }

    companion object {
        private const val TAG = "HomeViewModel"
        private const val KEY_NEWS_FETCH_AT = "news_fetch_at"
        private const val KEY_NEWS_CACHE = "news_cache"
        private val gson = Gson()
    }
}


@Immutable
data class HomeState(
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

sealed interface HomeSideEffect {
    class Toast(val message: String) : HomeSideEffect
}
