package com.pandatone.touchProtector

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout


class OverlayView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(ctx, attrs, defStyle) {
    companion object {
        /** Creates an instance of [OverlayView]. */
        fun create(context: Context) =
            View.inflate(context, R.layout.overlay_view, null) as OverlayView
    }

    private val windowManager: WindowManager =
        ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    /** Settings for overlay view */
    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Overlay レイヤに表示
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,  // 画面外への拡張を許可
        PixelFormat.TRANSLUCENT
    )

    /** Starts displaying this view as overlay. */
    @Synchronized
    fun show() {
        if (!this.isShown) {
            layoutParams.gravity = Gravity.TOP
            if (MainActivity.areaH != 0) {
                layoutParams.height = MainActivity.areaH
            }
            if (MainActivity.areaW != 0) {
                layoutParams.width = MainActivity.areaW
            }
            windowManager.addView(this, layoutParams)

        }
    }

    /** Hide this view. */
    @Synchronized
    fun hide() {
        if (this.isShown) {
            windowManager.removeView(this)
        }
    }

    fun through() {
        if (MainActivity.areaH != 0) {
            layoutParams.height = MainActivity.areaH
        }
        if (!OverlayService.THROUGH) {
            layoutParams.width = MainActivity.areaW
        } else {
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        }
        windowManager.removeView(this)
        windowManager.addView(this, layoutParams)
    }
}
