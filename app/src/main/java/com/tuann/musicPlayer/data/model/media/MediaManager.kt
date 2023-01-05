package com.tuann.musicPlayer.data.model.media


import android.media.MediaPlayer
import com.tuann.musicPlayer.ui.player.PlayerActivity
import com.tuann.musicPlayer.R
import com.tuann.musicPlayer.data.model.Song
import com.tuann.musicPlayer.data.model.formatDuration
import kotlin.system.exitProcess

object MediaManager : MediaPlayer.OnCompletionListener {
    val mediaPlayer = MediaPlayer()
    lateinit var musicListPA: ArrayList<Song>
    var songPosition: Int = 0
    var isPlaying: Boolean = false
    fun createMediaPlayer() {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(musicListPA[songPosition].path)
            mediaPlayer.prepare()
            mediaPlayer.start()
            isPlaying = true
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            PlayerActivity.binding.tvSeekBarStart.text =
                formatDuration(mediaPlayer.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text = formatDuration(mediaPlayer.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = mediaPlayer.duration
            mediaPlayer.setOnCompletionListener(this)
        } catch (e: Exception) {
            return
        }

    }

    fun playMusic() {
        isPlaying = true
        mediaPlayer.start()

    }

    fun pauseMusic() {
        isPlaying = false
        mediaPlayer.pause()
    }

    fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
        } else {
            setSongPosition(increment = false)
        }
    }

    fun setSongPosition(increment: Boolean) {
        if (!PlayerActivity.repeat) {
            if (increment) {
                if (musicListPA.size - 1 == songPosition)
                    songPosition = 0
                else ++songPosition
            } else {
                if (0 == songPosition)
                    songPosition = musicListPA.size - 1
                else --songPosition
            }
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
    }
    fun extiAppLication(){
        if (PlayerActivity.musicService != null)
            PlayerActivity.musicService!!.stopForeground(true)
        mediaPlayer.release()
        PlayerActivity.musicService = null
        exitProcess(1)
    }


}