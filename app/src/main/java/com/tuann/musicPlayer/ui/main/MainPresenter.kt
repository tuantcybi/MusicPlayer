package com.tuann.musicPlayer.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.tuann.musicPlayer.data.model.Song
import java.io.File

class MainPresenter(val mcontext: Context)
   {
       @SuppressLint("Recycle", "Range")
       @RequiresApi(Build.VERSION_CODES.R)
       fun getAllAudio(): ArrayList<Song> {
           val tempList = ArrayList<Song>()
           val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
           val projection = arrayOf(
               MediaStore.Audio.Media._ID,
               MediaStore.Audio.Media.TITLE,
               MediaStore.Audio.Media.ALBUM,
               MediaStore.Audio.Media.ARTIST,
               MediaStore.Audio.Media.DURATION,
               MediaStore.Audio.Media.DATE_ADDED,
               MediaStore.Audio.Media.DATA,
               MediaStore.Audio.Media.ALBUM_ID
           )


           val cursor = mcontext.contentResolver.query(
               /* uri = */ MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
               /* projection = */ projection,
               /* selection = */ selection,
               /* selectionArgs = */ null,
               /* sortOrder = */ MediaStore.Audio.Media.DATE_ADDED + " DESC",
               /* cancellationSignal = */ null
           )
           if (cursor != null) {
               if (cursor.moveToFirst()) do {
                   val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                       ?: "Unknown"
                   val idC =
                       cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)) ?: "Unknown"
                   val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                       ?: "Unknown"
                   val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                       ?: "Unknown"
                   val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                   val durationC =
                       cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                   val albumIdC =
                       cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                           .toString()
                   val uri = Uri.parse("content://media/external/audio/ablumart")
                   val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                   val song = Song(
                       id = idC,
                       title = titleC,
                       album = albumC,
                       artist = artistC,
                       path = pathC,
                       duration = durationC,
                       artUri = artUriC
                   )
                   val file = File(song.path)
                   if (file.exists()) tempList.add(song)
               } while (cursor.moveToNext())
               cursor.close()
           }
           return tempList
       }

    }
