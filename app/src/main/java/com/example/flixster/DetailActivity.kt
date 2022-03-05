package com.example.flixster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import android.widget.TextView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import okhttp3.Headers

private const val YOUTUBE_API_KEY = "AIzaSyCwPafMX9xyduviqWNIry3cFw-w-09upco"
private const val TRAILERS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed"
class DetailActivity : YouTubeBaseActivity() {

    private lateinit var tvTitle:TextView
    private lateinit var tvOverview:TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var ytPlayerView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        tvTitle = findViewById(R.id.tvTitle)
        tvOverview = findViewById(R.id.tvOverview)
        ratingBar = findViewById(R.id.rbBoteAverage)
        ytPlayerView = findViewById(R.id.player)


        val movie = intent.getParcelableExtra<Movie>(MOVIE_EXTRA) as Movie
        tvTitle.text = movie.title
        tvOverview.text = movie.overview
        ratingBar.rating = movie.voteAverage.toFloat()

        val client = AsyncHttpClient()
        client.get(TRAILERS_URL.format(movie.movieID), object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e("Jiayi", "onFailure $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                Log.e("Jiayi", "onSuccess")
                val result = json.jsonObject.getJSONArray("results")
                if (result.length() == 0) {
                    Log.w("Jiayi", "no Movie trailers found")
                    return
                }
                val movieTrailerJson = result.getJSONObject(0)
                val youtubeKey = movieTrailerJson.getString("key")

                initializeYoube(youtubeKey)
            }

        })
    }



    private fun initializeYoube(youtubeKey: String) {
        ytPlayerView.initialize(YOUTUBE_API_KEY,object : YouTubePlayer.OnInitializedListener{
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                p2: Boolean
            ) {
                Log.i("Jiayi", "onInitializationSuccess")
                if (ratingBar.rating >= 5){
                    player?.loadVideo(youtubeKey);
                }
                else{
                    player?.cueVideo(youtubeKey);
                }
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                TODO("Not yet implemented")
            }
        })
    }
}