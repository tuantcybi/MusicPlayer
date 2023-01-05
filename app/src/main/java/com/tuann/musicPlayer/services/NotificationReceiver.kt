package com.tuann.musicPlayer.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tuann.musicPlayer.ApplicationClass
import com.tuann.musicPlayer.ui.player.PlayerActivity
import com.tuann.musicPlayer.R
import com.tuann.musicPlayer.data.model.media.MediaManager

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS ->prevNextSong(increment = false, context = context!!)
            ApplicationClass.PLAY -> if(MediaManager.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(increment = true, context = context!!)
            ApplicationClass.EXIT -> {
               MediaManager.extiAppLication()
            }
        }
    }
    private fun playMusic(){
        MediaManager.isPlaying = true
        MediaManager.mediaPlayer.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
    }
    private fun pauseMusic(){
        MediaManager.isPlaying = true
        MediaManager.mediaPlayer.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
    }
    private fun prevNextSong(increment: Boolean, context: Context){
        MediaManager.setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()
        PlayerActivity.binding.songNamePA.text = MediaManager.musicListPA[MediaManager.songPosition].title
        playMusic()
    }
}