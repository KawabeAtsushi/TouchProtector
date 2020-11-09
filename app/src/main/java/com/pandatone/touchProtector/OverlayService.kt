package com.pandatone.touchProtector

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import kotlin.math.min

/**
 * A foreground service for managing the life cycle of overlay view.
 */
class OverlayService() : Service() {
    companion object {
        private const val ACTION_SHOW = "SHOW"
        private const val ACTION_HIDE = "HIDE"
        private const val ACTION_START = "START"
        private const val ACTION_STOP = "STOP"
        var THROUGH = false
        var transBackground = false
        var needChangeViews: Boolean = true

        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_START
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun show(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_SHOW
            }
            context.startService(intent)
        }

        fun hide(context: Context) {
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

        for ((i, view) in overlayViews.withIndex()) {
            MainActivity.viewModel.changePosition(i)
            setViews(view)
        }
    }

    /** Handles [ACTION_SHOW] and [ACTION_HIDE] intents. */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START -> {
                    isActive = true
                    needChangeViews = false
                    val viewModel = MainActivity.viewModel
                    for ((i, view) in overlayViews.withIndex()) {
                        viewModel.changePosition(i)
                        setViews(view)
                        view.show()
                    }
                    needChangeViews = true
                    viewModel.changePosition(0)
                }
                ACTION_STOP -> {
                    isActive = false
                    overlayViews.forEach { view -> view.hide() }
                    stopSelf()
                }
                ACTION_SHOW -> {
                    setViews(nowView)
                    nowView.show()
                }
                ACTION_HIDE -> {
                    nowView.hide()
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

        setIconSize(icon)
    }

    private fun setIconSize(icon: ImageView) {
        //アイコンサイズの変更
        val viewModel = MainActivity.viewModel
        val maxSize = min(viewModel.nowHeight, viewModel.nowWidth)
        val progress = viewModel.iconSize.value ?: 100
        val ratio = progress / 100f //(min)0 ~ 1(max)
        val size = ratio * maxSize
        icon.layoutParams.width = size.toInt()
        icon.layoutParams.height = size.toInt()
    }
}
