package com.example.exotest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity() {

    private var playerView: PlayerView? = null
    private lateinit var player: SimpleExoPlayer
    private var playbackPosition: Long = 0
    private val Uri = "https://livelinearhddash2.akamaized.net/dash/live/2003277/ch129/master.mpd"

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById<PlayerView>(R.id.Player_View)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            if (playerView != null) {
                playerView!!.onResume()
            }
        }
    }

    private fun initializePlayer() {

        player = SimpleExoPlayer.Builder( /* context= */this)
            .setMediaSourceFactory(
                ProgressiveMediaSource.Factory(
                    DefaultDataSourceFactory( /* context= */this),
                    DefaultExtractorsFactory().setMp4ExtractorFlags(
                        Mp4Extractor.FLAG_WORKAROUND_IGNORE_EDIT_LISTS
                    )
                )
            )
            .build()

        playerView?.player = player
        playerView?.keepScreenOn = true

        player.playWhenReady = true
        player.seekTo(playbackPosition)
        player.setMediaSource(

            DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri))

        )

        player.prepare()

    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            if (playerView != null) {
                playerView!!.onResume()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView!!.onPause()
            }
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        playbackPosition = player.currentPosition
        player.release()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView!!.onPause()
            }
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}