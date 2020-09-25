package com.pandatone.touchProtector

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.Toast

/**
 * A foreground service for managing the life cycle of overlay view.
 */
class OverlayService : Service() {
    companion object {
        private const val ACTION_SHOW = "SHOW"
        private const val ACTION_HIDE = "HIDE"
        var THROUGH = false
        var transBackground = false

        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_SHOW
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_HIDE
            }
            context.startService(intent)
        }

        // To control toggle button in MainActivity. This is not elegant but works.
        var isActive = false
            private set
    }

    private lateinit var overlayView: OverlayView

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Start as a foreground service
            val notification = MyNotification.build(this)
            startForeground(1, notification)
        }
        overlayView = OverlayView.create(this)
        setViews()
    }

    /** Handles [ACTION_SHOW] and [ACTION_HIDE] intents. */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SHOW -> {
                    isActive = true
                    overlayView.show()
                }
                ACTION_HIDE -> {
                    isActive = false
                    overlayView.hide()
                    stopSelf()
                }
                else -> MyLog.e("Need action property to start ${OverlayService::class.java.simpleName}")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /** Cleans up views just in case. */
    override fun onDestroy() = overlayView.hide()

    /** This service does not support binding. */
    override fun onBind(intent: Intent?) = null

    private fun setViews(){
        val cat = overlayView.findViewById<ImageView>(R.id.cat)
        val catThrough = overlayView.findViewById<ImageView>(R.id.cat_through)

        if (transBackground){
            cat.setBackgroundResource(0)
            catThrough.setBackgroundResource(0)
        }

        cat.setOnLongClickListener {
            cat.visibility = View.GONE
            catThrough.visibility = View.VISIBLE
            THROUGH = true
            overlayView.through()
            Toast.makeText(this,"透過モード:ON",Toast.LENGTH_SHORT).show()
            true
        }
        catThrough.setOnLongClickListener {
            cat.visibility = View.VISIBLE
            catThrough.visibility = View.GONE
            THROUGH = false
            overlayView.through()
            Toast.makeText(this,"透過モード:OFF",Toast.LENGTH_SHORT).show()
            true
        }
    }
}
