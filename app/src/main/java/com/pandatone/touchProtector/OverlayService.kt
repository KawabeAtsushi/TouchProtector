package com.pandatone.touchProtector

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.updatePadding
import kotlinx.android.synthetic.main.main_activity.view.*
import java.sql.Time

/**
 * A foreground service for managing the life cycle of overlay view.
 */
class OverlayService() : Service() {
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
    private lateinit var cat :ImageView
    private lateinit var catThrough :ImageView

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Start as a foreground service
            val notification = MyNotification.build(this)
            startForeground(1, notification)
        }
        overlayView = OverlayView.create(this)
        cat = overlayView.findViewById<ImageView>(R.id.cat)
        catThrough = overlayView.findViewById<ImageView>(R.id.cat_through)
        setViews()
        refresh()
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
        val catBackground = overlayView.findViewById<FrameLayout>(R.id.cat_background)
        val throughBackground = overlayView.findViewById<FrameLayout>(R.id.through_background)

        if (transBackground){
            catBackground.setBackgroundResource(0)
            throughBackground.setBackgroundResource(0)
        }

        cat.setOnLongClickListener {
            cat.visibility = View.GONE
            catBackground.visibility = View.GONE
            throughBackground.visibility = View.VISIBLE
            catThrough.visibility = View.VISIBLE
            THROUGH = true
            overlayView.through()
            Toast.makeText(this,R.string.back_on,Toast.LENGTH_SHORT).show()
            true
        }
        catThrough.setOnLongClickListener {
            cat.visibility = View.VISIBLE
            catBackground.visibility = View.VISIBLE
            catThrough.visibility = View.GONE
            throughBackground.visibility = View.GONE
            THROUGH = false
            overlayView.through()
            Toast.makeText(this,R.string.back_off,Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun refresh(){
        val size = MainActivity.viewModel.topSize.value?:0
        cat.layoutParams.width = size
        cat.layoutParams.height = size
    }
}
