package com.queentech.data.usecase.news

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
) : GetLotteryNewsUseCase {

    override suspend fun invoke(
        maxResults: Int,
        query: String
    ): Result<List<NewsArticle>> = runCatching {
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
        }
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
}