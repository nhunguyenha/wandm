package com.wandm.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.wandm.App
import com.wandm.R
import com.wandm.adapters.ArtistsAdapter
import com.wandm.loaders.ArtistLoader
import com.wandm.models.Artist
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.SortOrder
import com.wandm.views.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_artists.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ArtistsFragment : BaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_artists
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        PreferencesUtils.setArtistAlbumSortOrder(SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z)
        artistsRecyclerView.layoutManager = LinearLayoutManager(activity)
        artistsFastScroller.setRecyclerView(artistsRecyclerView)

        if (activity != null) {
            doAsync {
                val adapter = ArtistsAdapter(ArtistLoader.getAllArtists(App.instance) as ArrayList<Artist>)

                uiThread {
                    artistsRecyclerView.adapter = adapter
                    setItemDecoration()
                    artistsRecyclerView.adapter.notifyDataSetChanged()
                    artistsFastScroller.visibility = View.VISIBLE
                    artistsProgressBar.visibility = View.GONE
                }
            }
        }

    }

    private fun setItemDecoration() {
        artistsRecyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    }

}