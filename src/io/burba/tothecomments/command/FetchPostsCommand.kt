package io.burba.tothecomments.command

import io.burba.tothecomments.http.UrlDetails
import io.burba.tothecomments.source.meta.MetaSource
import io.burba.tothecomments.source.post.PostSource
import io.burba.tothecomments.util.coerceToUrl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.net.MalformedURLException

class InvalidUrlException(cause: Throwable) : Exception(cause)

class FetchPostsCommand(private val metaSource: MetaSource, private val postSources: Iterable<PostSource>) {
    @Throws(InvalidUrlException::class)
    suspend fun run(urlStr: String): UrlDetails = coroutineScope {
        val url = try {
            urlStr.coerceToUrl()
        } catch (e: MalformedURLException) {
            throw InvalidUrlException(e)
        }

        val metaAsync = async { metaSource.metaForUrl(url) }
        val postsAsync = postSources.map { source -> async { source.postsForUrl(url) } }

        val meta = metaAsync.await()
        val posts = postsAsync.awaitAll().flatten()

        UrlDetails(meta, posts)
    }
}