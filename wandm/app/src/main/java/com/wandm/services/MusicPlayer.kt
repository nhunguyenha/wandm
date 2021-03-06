package com.wandm.services

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.wandm.App
import com.wandm.IWMService

object MusicPlayer {
    private val TAG = "MusicPlayer"

    private var mService: IWMService? = null
    private var mCallback: Callback? = null
    var isServiceBound = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            mService = IWMService.Stub.asInterface(p1)
            mCallback?.onServiceConnected()
            isServiceBound = true
            mService?.playNew()
            Log.d(TAG, "Service connected")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mService = null
            mCallback?.onServiceDisconnected()
            isServiceBound = false
            Log.d(TAG, "Service disconnected")
        }
    }

    fun bind(callback: Callback?) {
        if (!isServiceBound) {
            val intent = Intent(App.instance, WMService::class.java)
            intent.action = IWMService::class.java.name
            App.instance.startService(intent)
            App.instance.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            mCallback = callback
        } else
            mService?.playNew()
    }

    fun unbind(context: Context) {
        if (isServiceBound) {
            context.unbindService(mConnection)
        }
    }

    fun next() {
        mService?.playNext()
    }

    fun pre() {
        mService?.playPre()
    }

    fun playOrPause() {
        if (mService != null) {
            if (mService!!.isPlaying)
                mService?.pause()
            else
                mService?.resume()
        }
    }

    fun position(): Int {
        return mService!!.position()
    }

    fun duration(): Int {
        return mService!!.duration()
    }

    fun seekTo(postion: Int) {
        mService?.seekTo(postion)
    }

    fun isPlaying(): Boolean {
        return mService!!.isPlaying
    }

    interface Callback {
        fun onServiceConnected()
        fun onServiceDisconnected()
    }
}
