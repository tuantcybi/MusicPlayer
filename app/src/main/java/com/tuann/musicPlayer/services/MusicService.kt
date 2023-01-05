package com.tuann.musicPlayer.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.tuann.musicPlayer.ApplicationClass
import com.tuann.musicPlayer.ui.main.MainActivity
import com.tuann.musicPlayer.ui.player.PlayerActivity
import com.tuann.musicPlayer.R
import com.tuann.musicPlayer.data.model.formatDuration
import com.tuann.musicPlayer.data.model.media.MediaManager
import com.tuann.musicPlayer.data.model.media.MediaManager.musicListPA
import com.tuann.musicPlayer.data.model.media.MediaManager.songPosition

class MusicService : Service() {

    private var myBinder = MyBinder()
    var mediamanager: MediaManager? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService;
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }


    fun showNotification(playPauseBtn: Int) {
        val intent = Intent(this, MainActivity::class.java)
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)
        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(
            ApplicationClass.PREVIOUS
        )
        val prevpendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(
            ApplicationClass.PLAY
        )
        val playpendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(
            ApplicationClass.NEXT
        )
        val nextpendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(
            ApplicationClass.EXIT
        )
        val exitpendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notification = NotificationCompat.Builder(this, ApplicationClass.CHANNEL_ID)
//            .setContentIntent(contentIntent)
            .setContentTitle(musicListPA[songPosition].title)
            .setContentText(musicListPA[songPosition].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.music_player_icon_slash_screen
                )
            )
//            .setStyle(
//                androidx.media.app.NotificationCompat.MediaStyle()
//                    .setMediaSession(mediaSession.sessionToken)
//            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon, "Previuos", prevpendingIntent)
            .addAction(playPauseBtn, "Play", playpendingIntent)
            .addAction(R.drawable.next_icon, "Next", nextpendingIntent)
            .addAction(R.drawable.exit_icon, "Exit", exitpendingIntent)
            .build()
        startForeground(13, notification)
    }

    fun createMediaPlayer() {
        MediaManager.createMediaPlayer()
    }
    fun seekBarSetup(){
        runnable = Runnable{
            PlayerActivity.binding.tvSeekBarStart.text = formatDuration(MediaManager.mediaPlayer.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress = MediaManager.mediaPlayer.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
}
