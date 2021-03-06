package com.wandm.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wandm.R
import com.wandm.activities.MainActivity
import com.wandm.data.CurrentPlaylistManager
import com.wandm.database.FavoritesTable
import com.wandm.database.MusicDBHandler
import com.wandm.dialogs.PlaylistsDialog
import com.wandm.models.Song
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.Utils
import com.wandm.views.BubbleTextGetter
import kotlinx.android.synthetic.main.item_song.view.*


class SongsAdapter(var listSongs: ArrayList<Song>,
                   private val isShowMenu: Boolean,
                   private val listener: (Song, Int, String) -> Unit) : RecyclerView.Adapter<SongsAdapter.SongHolder>(), BubbleTextGetter {

    companion object {
        val ACTION_PLAY = "action_play"
        val ACTION_ADD_FAVORITES = "action_add_favorites"
        val ACTION_ADD_PLAYLIST = "action_add_playlist"
        val ACTION_REMOVE_SONG = "action_remove_song"
    }

    override fun getTextToShowInBubble(pos: Int): String {
        if (listSongs.size > 0) {
            return listSongs[pos].title[0].toString()
        }

        return ""
    }

    override fun getItemCount(): Int {
        return listSongs.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SongHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_song, parent, false)
        return SongHolder(view)
    }

    override fun onBindViewHolder(holder: SongHolder?, position: Int) {
        holder?.bind(listSongs[position])

        holder?.songItemView?.setOnClickListener {
            CurrentPlaylistManager.listSongs = listSongs
            CurrentPlaylistManager.position = position
            listener(listSongs[position], position, ACTION_PLAY)
        }

        holder?.songItemView?.addPlaylistButton?.setOnClickListener {
            setupPopupMenu(MainActivity.instance, holder.songItemView.addPlaylistButton, position)
        }

        holder?.songItemView?.setOnLongClickListener {
            listener(listSongs[position], position, ACTION_REMOVE_SONG)
            true
        }
    }


    inner class SongHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songItemView: View = itemView

        fun bind(song: Song) {
            setupSize(itemView)
            var colorResId = R.color.color_dark_theme
            if (PreferencesUtils.getLightTheme())
                colorResId = R.color.color_light_theme

            itemView.addPlaylistButton.setColor(itemView?.context?.resources?.getColor(colorResId)!!)

            if (!isShowMenu)
                itemView.addPlaylistButton.visibility = View.GONE

            Picasso.with(itemView.context)
                    .load(Utils.getAlbumArtUri(song.albumId).toString())
                    .into(itemView.albumArt, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError() {
                            val art = song.art
                            if (art == null) {
                                itemView.albumArt.background = itemView.context.getDrawable(R.drawable.ic_action_music)
                                return
                            }

                            val songImage = BitmapFactory.decodeByteArray(art, 0, art.size)
                            if (songImage != null)
                                itemView.albumArt.setImageBitmap(songImage)
                            else itemView.albumArt.background = itemView.context.getDrawable(R.drawable.ic_action_music)
                        }
                    })

            itemView.titleItemSongTextView.text = song.title
            itemView.artistItemSongTextView.text = song.artistName

            itemView.titleItemSongTextView.isSelected = true
            itemView.artistItemSongTextView.isSelected = true

        }

    }

    private fun setupPopupMenu(context: Context, view: View, pos: Int) {
        val popup = PopupMenu(context, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.menu_song, popup.menu)
        popup.setOnMenuItemClickListener { handelMenuItemClick(context, it, pos) }
        popup.show()
    }

    private fun handelMenuItemClick(context: Context, it: MenuItem?, pos: Int): Boolean {
        when (it?.itemId) {
            R.id.addFavoritesItemMenu -> {
                listener(listSongs[pos], pos, ACTION_ADD_FAVORITES)
                MusicDBHandler.getInstance(context, FavoritesTable.TABLE_NAME)?.insert(listSongs[pos])
                Toast.makeText(context, context.getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show()
            }

            R.id.addPlaylistItemMenu -> {
                listener(listSongs[pos], pos, ACTION_ADD_PLAYLIST)
                val playlistsDialog = PlaylistsDialog.newInstance(listSongs[pos])
                val fragmentManager = MainActivity.instance.supportFragmentManager
                playlistsDialog.show(fragmentManager, PlaylistsDialog::javaClass.name)
            }
        }
        return true
    }

    private fun setupSize(itemView: View) {
        val textSize = PreferencesUtils.getTextSize()
        itemView.titleItemSongTextView.textSize = textSize.toFloat()
        itemView.artistItemSongTextView.textSize = (textSize - 4).toFloat()
    }
}