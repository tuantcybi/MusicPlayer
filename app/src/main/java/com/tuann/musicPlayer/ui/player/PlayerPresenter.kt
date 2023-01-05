package com.tuann.musicPlayer.ui.player

import android.content.Context
import androidx.core.content.ContextCompat
import com.tuann.musicPlayer.R
import com.tuann.musicPlayer.data.model.media.MediaManager

class PlayerPresenter(val context: Context ) {
    fun setLayout() {
        PlayerActivity.binding.songNamePA.text =
            MediaManager.musicListPA[MediaManager.songPosition].title
        if (PlayerActivity.repeat) PlayerActivity.binding.repeatBtnPA.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.purple_500
            )
        )
        if (PlayerActivity.min15 || PlayerActivity.min30 || PlayerActivity.min60) PlayerActivity.binding.timerBtnPA.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.purple_500
            )
        )

    }
}