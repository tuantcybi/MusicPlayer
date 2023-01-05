package com.tuann.musicPlayer.ui.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tuann.musicPlayer.R
import com.tuann.musicPlayer.data.model.media.MediaManager
import com.tuann.musicPlayer.data.model.media.MediaManager.musicListPA
import com.tuann.musicPlayer.data.model.media.MediaManager.pauseMusic
import com.tuann.musicPlayer.data.model.media.MediaManager.playMusic
import com.tuann.musicPlayer.data.model.media.MediaManager.prevNextSong
import com.tuann.musicPlayer.data.model.media.MediaManager.songPosition
import com.tuann.musicPlayer.databinding.ActivityPlayerBinding
import com.tuann.musicPlayer.services.MusicService

class PlayerActivity : AppCompatActivity(), ServiceConnection, PlayerContract.View {

    companion object {
        var musicService: MusicService? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var playerPresenter: PlayerPresenter
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerPresenter = PlayerPresenter(this)
        setTheme(R.style.coolPinkNav)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Starting service
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        val bindService = bindService(intent, this, BIND_AUTO_CREATE)
        initalizeLayout()
        binding.backBtnPA.setOnClickListener {
            finish()
        }
        binding.playPauseBtnPA.setOnClickListener {
            if (MediaManager.isPlaying) {
                pauseMusic()
                binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
                musicService!!.showNotification(R.drawable.play_icon)
            } else {
                playMusic()
                binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
                musicService!!.showNotification(R.drawable.pause_icon)
            }
        }
        binding.previousBtnPA.setOnClickListener {
            prevNextSong(increment = false)
            playerPresenter.setLayout()
            MediaManager.createMediaPlayer()
        }
        binding.nextBtnPA.setOnClickListener {
            prevNextSong(increment = true)
            playerPresenter.setLayout()
            MediaManager.createMediaPlayer()
        }
        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) MediaManager.mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
        binding.repeatBtnPA.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            } else {
                repeat = false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
            }
        }
        binding.equalizerBtnPA.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    MediaManager.mediaPlayer.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 13)
            } catch (e: Exception) {
                Toast.makeText(this, "Equalizẻ Feature not Supported!!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.timerBtnPA.setOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer)
                showBottomSheetDialog()
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Exit")
                    .setMessage("Do you want to stop time?")
                    .setPositiveButton("Yes") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.timerBtnPA.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.cool_pink
                            )
                        )
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }
        binding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))

        }

    }

    fun initalizeLayout() {

        MediaManager.songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                MediaManager.musicListPA.addAll(MediaManager.musicListPA)
                playerPresenter.setLayout()

            }
            "MainActivity" -> {
                MediaManager.musicListPA.addAll(MediaManager.musicListPA)
                MediaManager.musicListPA.shuffle()
                playerPresenter.setLayout()

            }
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        MediaManager.createMediaPlayer()
        musicService!!.seekBarSetup()


    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK)
            return

    }

    override fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 15 minutes", Toast.LENGTH_SHORT)
                .show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min15 = true
            Thread {
                Thread.sleep(15 * 60000)
                if (min15) MediaManager.extiAppLication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 30 minutes", Toast.LENGTH_SHORT)
                .show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min30 = true
            Thread {
                Thread.sleep(30 * 60000)
                if (min30) MediaManager.extiAppLication()
                dialog.dismiss()
            }
            dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
                Toast.makeText(baseContext, "Music will stop after 60 minutes", Toast.LENGTH_SHORT)
                    .show()
                binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
                min60 = true
                Thread {
                    Thread.sleep(60 * 60000)
                    if (min60) MediaManager.extiAppLication()
                    dialog.dismiss()
                }
            }
        }
    }
}
