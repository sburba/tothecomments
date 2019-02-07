package io.burba.tothecomments.source.post

import io.burba.tothecomments.Post
import io.burba.tothecomments.source.FetchException

class HnPostSource(private val hnApi: HnApi): PostSource {
    override fun postsForUrl(url: String): List<Post> {
        val response = hnApi.getPosts(url).execute()
        if (response.isSuccessful) {
            val body = response.body() ?: throw FetchException("Received empty response body")
            return body.hits.map { Post("https://news.ycombinator.com/item?id=${it.objectID}", it.title) }
        } else {
            throw FetchException(
                response.errorBody()?.string() ?: "Unable to fetch requested data"
            )
        }
    }
}