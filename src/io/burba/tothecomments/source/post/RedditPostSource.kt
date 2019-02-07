package io.burba.tothecomments.source.post

import io.burba.tothecomments.Post
import io.burba.tothecomments.source.FetchException
import net.dean.jraw.RedditClient
import net.dean.jraw.RedditException
import net.dean.jraw.http.NetworkException
import net.dean.jraw.models.SearchSort

class RedditPostSource(private val reddit: RedditClient) : PostSource {

    override fun postsForUrl(url: String): List<Post> {
        val pager = reddit.search()
            .query("""url:"$url"""")
            .limit(5)
            .sorting(SearchSort.TOP)
            .build()

        return try {
            pager.next().map { Post("https://reddit.com${it.permalink}", it.title, it.thumbnail, it.subreddit) }
        } catch (e: NetworkException) {
            throw FetchException(cause = e)
        } catch (e: RedditException) {
            throw FetchException(cause = e)
        }
    }
}