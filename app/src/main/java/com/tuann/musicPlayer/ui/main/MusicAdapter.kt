package com.tuann.musicPlayer.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tuann.musicPlayer.data.model.Song
import com.tuann.musicPlayer.data.model.formatDuration
import com.tuann.musicPlayer.databinding.MusicViewBinding
import com.tuann.musicPlayer.ui.player.PlayerActivity

class MusicAdapter(private val context: Context, private val musicList: ArrayList<Song>) :
    RecyclerView.Adapter<MusicAdapter.MyHolder>() {
    class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val albums = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))

    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.albums.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter")
            ContextCompat.startActivity(context,intent, null)
            ContextCompat.startForegroundService(context,intent)
        }


    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}