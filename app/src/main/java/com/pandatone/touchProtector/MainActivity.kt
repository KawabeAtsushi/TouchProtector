package com.pandatone.touchProtector

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity


/**
 * オーバーレイ表示を開始／終了するためのトグルボタンを表示します。
 */
class MainActivity : AppCompatActivity() {
    companion object {
        /** ID for the runtime permission dialog */
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
        var areaH = 200
        var areaW = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        requestOverlayPermission()

        val heightEt = findViewById<EditText>(R.id.height_edit)
        val widthEt = findViewById<EditText>(R.id.width_edit)
        val widthTv = findViewById<TextView>(R.id.width)
        val heightTv = findViewById<TextView>(R.id.height)
        val transCheck = findViewById<CheckBox>(R.id.transparent_check)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val dWidth = dm.widthPixels
        areaW = dWidth
        widthTv.text = "width : $areaW"
        heightTv.text = "height : $areaH"

        // Show/hide overlay view with a toggle button.
        findViewById<ToggleButton>(R.id.toggle_button).apply {
            isChecked = OverlayService.isActive
            setOnCheckedChangeListener { _, isChecked ->
                val heightStr = heightEt.text.toString()
                if(heightStr != ""){
                    areaH = Integer.parseInt(heightStr)
                }
                val widthStr = widthEt.text.toString()
                if(widthStr != ""){
                    areaW = Integer.parseInt(widthStr)
                }
                widthTv.text = "width : $areaW"
                heightTv.text = "height : $areaH"
                OverlayService.transBackground = transCheck.isChecked
                if (isChecked) OverlayService.start(this@MainActivity)
                else OverlayService.stop(this@MainActivity)
            }
        }
    }

    /** Requests an overlay permission to the user if needed. */
    private fun requestOverlayPermission() {
        if (isOverlayGranted()) return
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }

    /** Terminates the app if the user does not accept an overlay. */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!isOverlayGranted()) {
                finish()  // Cannot continue if not granted
            }
        }
    }

    /** Checks if the overlay is permitted. */
    private fun isOverlayGranted() =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)
}
