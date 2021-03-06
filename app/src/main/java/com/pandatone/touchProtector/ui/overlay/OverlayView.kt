package com.pandatone.touchProtector.ui.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.pandatone.touchProtector.KeyStore
import com.pandatone.touchProtector.R
import com.pandatone.touchProtector.ui.view.SettingFragment


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

    private val viewModel = SettingFragment.viewModel

    private val windowManager: WindowManager =
        ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    /** Settings for overlay view */
    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Overlay レイヤに表示
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,  // 画面外への拡張を許可
        PixelFormat.TRANSLUCENT
    )

    /** Starts displaying this view as overlay. */
    @Synchronized
    fun show() {
        if (!this.isShown && viewModel.nowVisible) {
            layoutParams.gravity = when (viewModel.nowPos.value!!) {
                KeyStore.TOP -> Gravity.TOP
                KeyStore.BOTTOM -> Gravity.BOTTOM
                KeyStore.RIGHT -> Gravity.END
                else -> Gravity.START
            }
            layoutParams.height = viewModel.nowHeight
            layoutParams.width = viewModel.nowWidth
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

    fun through(backOperation: Boolean, position: String) {

        if (!backOperation) {
            layoutParams.height = when (position) {
                KeyStore.TOP -> viewModel.topHeight.value ?: 0
                KeyStore.BOTTOM -> viewModel.bottomHeight.value ?: 0
                KeyStore.RIGHT -> viewModel.rightHeight.value ?: 0
                else -> viewModel.leftHeight.value ?: 0
            }
            layoutParams.width = when (position) {
                KeyStore.TOP -> viewModel.topWidth.value ?: 0
                KeyStore.BOTTOM -> viewModel.bottomWidth.value ?: 0
                KeyStore.RIGHT -> viewModel.rightWidth.value ?: 0
                else -> viewModel.leftWidth.value ?: 0
            }
        } else {
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        }
        windowManager.removeView(this)
        windowManager.addView(this, layoutParams)
    }

}
