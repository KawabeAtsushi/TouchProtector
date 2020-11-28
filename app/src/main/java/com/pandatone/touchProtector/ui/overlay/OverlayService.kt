package com.pandatone.touchProtector.ui.overlay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.pandatone.touchProtector.KeyStore
import com.pandatone.touchProtector.MyLog
import com.pandatone.touchProtector.MyNotification
import com.pandatone.touchProtector.R
import com.pandatone.touchProtector.ui.view.HomeFragment
import com.pandatone.touchProtector.ui.view.SettingFragment
import kotlin.math.min

/**
 * A foreground service for managing the life cycle of overlay view.
 */
class OverlayService : Service() {
    companion object {
        private const val ACTION_SHOW = "SHOW"
        private const val ACTION_HIDE = "HIDE"
        private const val ACTION_START = "START"
        private const val ACTION_STOP = "STOP"
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
            return when (SettingFragment.viewModel.nowPos.value!!) {
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
        rightOverlayView = OverlayView.create(this)
        bottomOverlayView = OverlayView.create(this)
        leftOverlayView = OverlayView.create(this)

        //※追加順重要
        overlayViews = ArrayList()
        overlayViews.add(topOverlayView)
        overlayViews.add(rightOverlayView)
        overlayViews.add(bottomOverlayView)
        overlayViews.add(leftOverlayView)

        val viewModel = SettingFragment.viewModel
        for ((i, view) in overlayViews.withIndex()) {
            viewModel.changePosition(i)
            setViews(view, viewModel.nowPos.value!!)
        }
    }

    /** Handles [ACTION_SHOW] and [ACTION_HIDE] intents. */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val viewModel = SettingFragment.viewModel

            when (it.action) {
                ACTION_START -> {
                    isActive = true
                    needChangeViews = false
                    for ((i, view) in overlayViews.withIndex()) {
                        viewModel.changePosition(i)
                        setViews(view, viewModel.nowPos.value!!)
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
                    setViews(nowView, viewModel.nowPos.value!!)
                    nowView.show()
                }
                ACTION_HIDE -> {
                    nowView.hide()
                }
                else -> MyLog.e("Need action property to start ${OverlayService::class.java.simpleName}")
            }

            HomeFragment.viewModel.setToggleStatus(isActive)

        }
        return super.onStartCommand(intent, flags, startId)
    }

    /** Cleans up views just in case. */
    override fun onDestroy() {
        overlayViews.forEach { view -> view.hide() }
    }

    /** This service does not support binding. */
    override fun onBind(intent: Intent?): Nothing? = null

    private fun setViews(overlayView: OverlayView, position: String) {

        val icon = overlayView.findViewById<ImageView>(R.id.overlay_icon)
        val iconThrough = overlayView.findViewById<ImageView>(R.id.cat_through)
        val background = overlayView.findViewById<FrameLayout>(R.id.overlay_background)
        val throughBackground = overlayView.findViewById<FrameLayout>(R.id.through_background)

        setViewInfo(background, icon, iconThrough)

        if (transBackground) {
            background.setBackgroundResource(0)
        }

        thruVisible(false, icon, background, iconThrough, throughBackground)

        icon.setOnLongClickListener {
            thruVisible(true, icon, background, iconThrough, throughBackground)
            overlayView.through(true, position)
            Toast.makeText(this, R.string.back_on, Toast.LENGTH_SHORT).show()
            true
        }
        iconThrough.setOnLongClickListener {
            thruVisible(false, icon, background, iconThrough, throughBackground)
            overlayView.through(false, position)
            Toast.makeText(this, R.string.back_off, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setViewInfo(background: FrameLayout, icon: ImageView, iconTrough: ImageView) {
        val viewModel = HomeFragment.viewModel

        val backgroundColor = viewModel.nowColor.value ?: 0
        background.setBackgroundResource(backgroundColor)

        icon.setImageResource(viewModel.nowIcon.value!!)
        iconTrough.setImageResource(viewModel.nowIcon.value!!)
        setIconSize(icon)
    }

    private fun thruVisible(thru: Boolean, normIc: View, normBg: View, thruIc: View, thruBg: View) {
        normIc.visibility = if (thru) View.GONE else View.VISIBLE
        normBg.visibility = if (thru) View.GONE else View.VISIBLE
        thruIc.visibility = if (thru) View.VISIBLE else View.GONE
        thruBg.visibility = if (thru) View.VISIBLE else View.GONE
    }

    private fun setIconSize(icon: ImageView) {
        //アイコンサイズの変更
        val viewModel = SettingFragment.viewModel
        val maxSize = min(viewModel.nowHeight, viewModel.nowWidth)
        val progress = HomeFragment.viewModel.iconSize.value ?: 100
        val ratio = progress / 100f //(min)0 ~ 1(max)
        val size = ratio * maxSize
        icon.layoutParams.width = size.toInt()
        icon.layoutParams.height = size.toInt()
    }
}
