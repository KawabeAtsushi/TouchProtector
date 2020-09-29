package com.pandatone.touchProtector

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pandatone.touchProtector.databinding.MainActivityBinding


/**
 * オーバーレイ表示を開始／終了するためのトグルボタンを表示します。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var heightEt: EditText
    private lateinit var widthEt: EditText
    private lateinit var widthTv: TextView
    private lateinit var heightTv: TextView
    private lateinit var transCheck: CheckBox

    companion object {
        lateinit var viewModel: MainViewModel

        /** ID for the runtime permission dialog */
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
        private const val TOP = "top"
        private const val BOTTOM = "bottom"
        private const val LEFT = "left"
        private const val RIGHT = "right"

        //プリファレンスキー
        private const val PREF_NAME = "my_settings"
        private const val FIRST_DATE = "firstDate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: MainActivityBinding = MainActivityBinding.inflate(layoutInflater)
        viewModel = MainViewModel()
        binding.viewModel = viewModel
        setContentView(binding.root)
        requestOverlayPermission()

        setViews()

        val pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val firstDate = pref.getLong(FIRST_DATE, 0)
        if (firstDate == 0L) {
            initialBoot()
        } else {
            viewModel.setTopParam(pref.getInt("topHeight", 0), pref.getInt("topWidth", 0))
            viewModel.setBottomParam(pref.getInt("bottomHeight", 0), pref.getInt("bottomWidth", 0))
            viewModel.setRightParam(pref.getInt("rightHeight", 0), pref.getInt("rightWidth", 0))
            viewModel.setLeftParam(pref.getInt("leftHeight", 0), pref.getInt("leftWidth", 0))
        }

        val statusView = findViewById<TextView>(R.id.status)
        val lastDay = System.currentTimeMillis() - pref.getLong(FIRST_DATE, 0)
        val limit = 72 * 3600 * 1000
        if (lastDay > limit) {
            statusView.text = getString(R.string.status_unlimited)
        } else {
            val minutes = (limit - lastDay) / 60000
            statusView.text =
                getString(R.string.status_trial) + (minutes / 60).toString() +
                        getString(R.string.hours) + (minutes % 60).toString() + getString(R.string.mins)
        }

        // Show/hide overlay view with a toggle button.
        findViewById<ToggleButton>(R.id.toggle_button).apply {
            isChecked = OverlayService.isActive
            setOnCheckedChangeListener { _, isChecked ->
                val heightStr = heightEt.text.toString()
                if (heightStr != "") {
                    setParams(TOP, height = Integer.parseInt(heightStr))
                }
                val widthStr = widthEt.text.toString()
                if (widthStr != "") {
                    setParams(TOP, width = Integer.parseInt(widthStr))
                }
                OverlayService.transBackground = transCheck.isChecked
                if (isChecked) OverlayService.start(this@MainActivity)
                else OverlayService.stop(this@MainActivity)
            }
        }
    }

    private fun setViews() {
        heightEt = findViewById<EditText>(R.id.height_edit)
        widthEt = findViewById<EditText>(R.id.width_edit)
        widthTv = findViewById<TextView>(R.id.width)
        heightTv = findViewById<TextView>(R.id.height)
        transCheck = findViewById<CheckBox>(R.id.transparent_check)
    }

    //アプリ初回起動時の処理
    private fun initialBoot() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val dWidth = dm.widthPixels
        val dHeight = dm.heightPixels
        setParams(TOP, height = 200, width = dWidth)
        setParams(BOTTOM, height = 200, width = dWidth)
        setParams(RIGHT, height = dHeight, width = 100)
        setParams(LEFT, height = dHeight, width = 100)
        getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().apply {
            putLong(FIRST_DATE, System.currentTimeMillis())
            apply()
        }
        Toast.makeText(this, "First", Toast.LENGTH_SHORT).show()
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
    private fun isOverlayGranted() = Settings.canDrawOverlays(this)

    private fun setParams(position: String, height: Int = -1, width: Int = -1) {

        when (position) {
            TOP -> viewModel.setTopParam(height, width)
            BOTTOM -> viewModel.setBottomParam(height, width)
            RIGHT -> viewModel.setRightParam(height, width)
            else -> viewModel.setLeftParam(height, width)
        }
        //プリファレンス（設定）に保存
        getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().apply {
            if (height > 0) putInt(position + "Height", height)
            if (width > 0) putInt(position + "Width", width)
            apply()
        }
    }
}
