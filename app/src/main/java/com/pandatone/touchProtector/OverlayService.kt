package com.pandatone.touchProtector

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast

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

    private lateinit var topOverlayView: OverlayView
    private lateinit var bottomOverlayView: OverlayView
    private lateinit var rightOverlayView: OverlayView
    private lateinit var leftOverlayView: OverlayView
    private lateinit var overlayViews: ArrayList<OverlayView>

    private val nowView: OverlayView
        get() {
            return when (MainActivity.viewModel.nowPos.value!!) {
                KeyStore.TOP -> topOverlayView
                KeyStore.BOTTOM -> bottomOverlayView
                KeyStore.RIGHT -> rightOverlayView
                else -> leftOverlayView
            }
        }

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Start as a foreground service
            val notification = MyNotification.build(this)
            startForeground(1, notification)
        }
        topOverlayView = OverlayView.create(this)
        bottomOverlayView = OverlayView.create(this)
        rightOverlayView = OverlayView.create(this)
        leftOverlayView = OverlayView.create(this)

        overlayViews = ArrayList()
        overlayViews.add(topOverlayView)
        overlayViews.add(bottomOverlayView)
        overlayViews.add(rightOverlayView)
        overlayViews.add(leftOverlayView)

        overlayViews.forEach {  view -> setViews(view)}
    }

    /** Handles [ACTION_SHOW] and [ACTION_HIDE] intents. */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SHOW -> {
                    isActive = true
                    nowView.show()
                }
                ACTION_HIDE -> {
                    isActive = false
                    nowView.hide()
                    stopSelf()
                }
                else -> MyLog.e("Need action property to start ${OverlayService::class.java.simpleName}")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /** Cleans up views just in case. */
    override fun onDestroy() {
        overlayViews.forEach { view -> view.hide() }
    }

    /** This service does not support binding. */
    override fun onBind(intent: Intent?) = null

    private fun setViews(overlayView: OverlayView) {

        val icon = overlayView.findViewById<ImageView>(R.id.cat)
        val iconThrough = overlayView.findViewById<ImageView>(R.id.cat_through)
        val iconBackground = overlayView.findViewById<FrameLayout>(R.id.cat_background)
        val throughBackground = overlayView.findViewById<FrameLayout>(R.id.through_background)

        if (transBackground) {
            iconBackground.setBackgroundResource(0)
            throughBackground.setBackgroundResource(0)
        }

        icon.setOnLongClickListener {
            icon.visibility = View.GONE
            iconBackground.visibility = View.GONE
            throughBackground.visibility = View.VISIBLE
            iconThrough.visibility = View.VISIBLE
            THROUGH = true
            overlayView.through()
            Toast.makeText(this, R.string.back_on, Toast.LENGTH_SHORT).show()
            true
        }
        iconThrough.setOnLongClickListener {
            icon.visibility = View.VISIBLE
            iconBackground.visibility = View.VISIBLE
            iconThrough.visibility = View.GONE
            throughBackground.visibility = View.GONE
            THROUGH = false
            overlayView.through()
            Toast.makeText(this, R.string.back_off, Toast.LENGTH_SHORT).show()
            true
        }

        refresh(icon)
    }

    private fun refresh(icon: ImageView) {
        val size = MainActivity.viewModel.topSize.value ?: 0
        icon.layoutParams.width = size
        icon.layoutParams.height = size
    }
}
