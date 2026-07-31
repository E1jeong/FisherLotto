package com.queentech.data.usecase.news

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.queentech.data.database.datastore.NewsLocalDataSource
import com.queentech.domain.model.news.NewsArticle
import com.queentech.domain.usecase.news.GetLotteryNewsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.net.URLEncoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class GetLotteryNewsUseCaseImpl @Inject constructor(
    private val client: OkHttpClient,
    private val newsLocalDataSource: NewsLocalDataSource,
) : GetLotteryNewsUseCase {

    override suspend fun invoke(
        maxResults: Int,
        query: String,
        forceRefresh: Boolean,
    ): Result<List<NewsArticle>> = runCatching {
        if (!forceRefresh) {
            cachedNews(maxResults)?.let { return@runCatching it }
        }

        withContext(Dispatchers.IO) {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = "https://news.google.com/rss/search?q=$encoded&hl=ko&gl=KR&ceid=KR:ko"

            val req = Request.Builder()
                .url(url)
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Android) FisherLotto/1.0 (NewsFetcher)"
                )
                .build()

            val xml = client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) error("News RSS HTTP ${resp.code}")
                resp.body?.string().orEmpty()
            }

            parseGoogleNewsRss(xml, maxResults)
        }.also { news ->
            newsLocalDataSource.saveCache(gson.toJson(news), System.currentTimeMillis())
        }
    }

    // 캐시가 없거나 만료됐거나 비어 있으면 null을 돌려 네트워크 조회로 넘긴다.
    private suspend fun cachedNews(maxResults: Int): List<NewsArticle>? {
        val cache = newsLocalDataSource.getCache() ?: return null
        if (System.currentTimeMillis() - cache.fetchedAtEpochMillis >= CACHE_TTL_MILLIS) return null

        val type = object : TypeToken<List<NewsArticle>>() {}.type
        val cached = runCatching {
            gson.fromJson<List<NewsArticle>>(cache.json, type)
        }.getOrNull()

        return cached?.takeIf { it.isNotEmpty() }?.take(maxResults)
    }

    private fun parseGoogleNewsRss(xml: String, maxResults: Int): List<NewsArticle> {
        val doc = Jsoup.parse(xml, "", Parser.xmlParser())
        val items = doc.select("item")

        val fmt = DateTimeFormatter.RFC_1123_DATE_TIME
        val locale = Locale.ENGLISH

        return items.mapNotNull { item ->
            val title = item.selectFirst("title")?.text()?.trim().orEmpty()
            val link = item.selectFirst("link")?.text()?.trim().orEmpty()
            if (title.isBlank() || link.isBlank()) return@mapNotNull null

            val descriptionRaw = item.selectFirst("description")?.text().orEmpty()
            val summary = Jsoup.parse(descriptionRaw).text().trim()

            val source =
                item.selectFirst("source")?.text()?.trim()
                    ?: runCatching { java.net.URI(link).host ?: "" }.getOrDefault("")

            val pubDate = item.selectFirst("pubDate")?.text()?.trim().orEmpty()
            val publishedAt = runCatching {
                ZonedDateTime.parse(pubDate, fmt.withLocale(locale)).toInstant().toEpochMilli()
            }.getOrDefault(0L)

            NewsArticle(
                title = title,
                link = link,
                source = source,
                publishedAtEpochMillis = publishedAt,
                summary = summary,
            )
        }
            .distinctBy { it.link }
            .take(maxResults)
    }

    companion object {
        private const val CACHE_TTL_MILLIS = 30 * 60 * 1000L
        private val gson = Gson()
    }
}