package com.tuann.musicPlayer.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tuann.musicPlayer.R
import com.tuann.musicPlayer.data.model.media.MediaManager
import com.tuann.musicPlayer.data.model.media.MediaManager.musicListPA
import com.tuann.musicPlayer.databinding.ActivityMainBinding
import com.tuann.musicPlayer.ui.favourite.FavouriteActivity
import com.tuann.musicPlayer.ui.player.PlayerActivity
import com.tuann.musicPlayer.ui.playlist.PlaylistActivity

@Suppress("UNUSED_EXPRESSION")
public class MainActivity : AppCompatActivity() {
    private lateinit var mainPresenter: MainPresenter
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter


    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainPresenter = MainPresenter(this)
        setTheme(R.style.coolPinkNav)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //for nav drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (requestRuntimePermission()) {
            inittializeLayout()
            binding.shuffleBtn.setOnClickListener {
                val intent = Intent(this@MainActivity, PlayerActivity::class.java)
                intent.putExtra("index", 0)
                intent.putExtra("class", "MainActivity")
                startActivity(intent)
            }
            binding.favouriteBtn.setOnClickListener {
                startActivity(Intent(this@MainActivity, FavouriteActivity::class.java))

            }
            binding.playListBtn.setOnClickListener {
                startActivity(Intent(this@MainActivity, PlaylistActivity::class.java))
            }
            binding.navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navFeedback -> Toast.makeText(baseContext, "FeedBack", Toast.LENGTH_SHORT)
                        .show()
                    R.id.navSettings -> Toast.makeText(baseContext, "Settings", Toast.LENGTH_SHORT)
                        .show()
                    R.id.navAbout -> Toast.makeText(baseContext, "About", Toast.LENGTH_SHORT).show()
                    R.id.navExit -> {
                        val builder = MaterialAlertDialogBuilder(this)
                        builder.setTitle("Exit")
                            .setMessage("Do you want to close app?")
                            .setPositiveButton("Yes"){_, _ ->
                             MediaManager.extiAppLication()
                            }
                            .setNegativeButton("No"){dialog, _ ->
                                dialog.dismiss()
                            }
                        val customDialog = builder.create()
                        customDialog.show()
                        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                        customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                    }
                }
                true
            }

        }
    }

    private fun requestRuntimePermission(): Boolean {
        if (ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                inittializeLayout()
            } else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun inittializeLayout() {
        musicListPA = mainPresenter.getAllAudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, musicListPA)
        binding.musicRV.adapter = musicAdapter
        binding.totalSong.text = "Total Songs : " + musicAdapter.itemCount
    }
//    @SuppressLint("Recycle", "Range")
//    @RequiresApi(Build.VERSION_CODES.R)
//    fun getAllAudio(): ArrayList<Song> {
//        val tempList = ArrayList<Song>()
//        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
//        val projection = arrayOf(
//            MediaStore.Audio.Media._ID,
//            MediaStore.Audio.Media.TITLE,
//            MediaStore.Audio.Media.ALBUM,
//            MediaStore.Audio.Media.ARTIST,
//            MediaStore.Audio.Media.DURATION,
//            MediaStore.Audio.Media.DATE_ADDED,
//            MediaStore.Audio.Media.DATA,
//            MediaStore.Audio.Media.ALBUM_ID
//        )
//
//        val cursor = this.contentResolver.query(
//            /* uri = */ MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            /* projection = */ projection,
//            /* selection = */ selection,
//            /* selectionArgs = */ null,
//            /* sortOrder = */ MediaStore.Audio.Media.DATE_ADDED + " DESC",
//            /* cancellationSignal = */ null
//        )
//        if (cursor != null) {
//            if (cursor.moveToFirst()) do {
//                val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
//                    ?: "Unknown"
//                val idC =
//                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)) ?: "Unknown"
//                val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
//                    ?: "Unknown"
//                val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
//                    ?: "Unknown"
//                val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
//                val durationC =
//                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
//                val albumIdC =
//                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
//                        .toString()
//                val uri = Uri.parse("content://media/external/audio/ablumart")
//                val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
//                val song = Song(
//                    id = idC,
//                    title = titleC,
//                    album = albumC,
//                    artist = artistC,
//                    path = pathC,
//                    duration = durationC,
//                    artUri = artUriC
//                )
//                val file = File(song.path)
//                if (file.exists()) tempList.add(song)
//            } while (cursor.moveToNext())
//            cursor.close()
//        }
//        return tempList
//    }



    override fun onDestroy() {
        super.onDestroy()
        if (MediaManager.isPlaying && PlayerActivity.musicService != null){
            MediaManager.extiAppLication()
        }
    }

}

