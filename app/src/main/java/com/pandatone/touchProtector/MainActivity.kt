package com.pandatone.touchProtector

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pandatone.touchProtector.databinding.MainActivityBinding
import kotlin.math.min


/**
 * オーバーレイ表示を開始／終了するためのトグルボタンを表示します。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var switch: ToggleButton
    private lateinit var hEdit: EditText
    private lateinit var wEdit: EditText
    private lateinit var wText: TextView
    private lateinit var hText: TextView
    private lateinit var transCheck: CheckBox

    companion object {
        lateinit var viewModel: MainViewModel

        /** ID for the runtime permission dialog */
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: MainActivityBinding = MainActivityBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        requestOverlayPermission()

        setViews()

        val pref = getSharedPreferences(PREF.Name.key, MODE_PRIVATE)
        val firstDate = pref.getLong(PREF.FirstDate.key, 0)
        if (firstDate == 0L) {
            initialBoot()
        } else {
            viewModel.setTopParam(pref.getInt(PREF.TopH.key, 0), pref.getInt(PREF.TopW.key, 0))
            viewModel.setBottomParam(pref.getInt(PREF.TopH.key, 0), pref.getInt(PREF.TopH.key, 0))
            viewModel.setRightParam(pref.getInt(PREF.TopH.key, 0), pref.getInt(PREF.TopH.key, 0))
            viewModel.setLeftParam(pref.getInt(PREF.TopH.key, 0), pref.getInt(PREF.TopH.key, 0))
        }
        changeIconSize(pref.getInt("topHeight", 0), pref.getInt("topWidth", 0), 100)

        val statusView = findViewById<TextView>(R.id.status)
        val lastDay = System.currentTimeMillis() - pref.getLong(PREF.FirstDate.key, 0)
        val limit = 72 * 3600 * 1000
        if (lastDay > limit) {
            statusView.text = getString(R.string.status_unlimited)
        } else {
            val minutes = (limit - lastDay) / 60000
            statusView.text =
                getString(R.string.status_trial) + (minutes / 60).toString() +
                        getString(R.string.hours) + (minutes % 60).toString() + getString(R.string.mins)
        }

        // ON/OFFのトグルボタン切り替え
        switch.apply {
            isChecked = OverlayService.isActive
            setOnCheckedChangeListener { _, isChecked ->
                val heightStr = hEdit.text.toString()
                if (heightStr != "") {
                    setParams(viewModel.nowPos.value!!, height = Integer.parseInt(heightStr))
                }
                val widthStr = wEdit.text.toString()
                if (widthStr != "") {
                    setParams(viewModel.nowPos.value!!, width = Integer.parseInt(widthStr))
                }
                OverlayService.transBackground = transCheck.isChecked
                //プリファレンス（設定）に保存
                getSharedPreferences(PREF.Name.key, MODE_PRIVATE).edit().apply {
                    putBoolean(viewModel.nowPos.value!! + "Active", isChecked)
                    apply()
                }
                if (isChecked) OverlayService.start(this@MainActivity)
                else OverlayService.stop(this@MainActivity)
            }
        }

        findViewById<SeekBar>(R.id.seekBar).setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                //ツマミがドラッグされると呼ばれる
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    changeIconSize(
                        pref.getInt("topHeight", 0),
                        pref.getInt("topWidth", 0),
                        progress
                    )
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // ツマミがタッチされた時に呼ばれる
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // ツマミがリリースされた時に呼ばれる
                }
            }
        )

        // ここでViewModelから流れてきた値を受け取る.
        viewModel.nowPos.observe(this, Observer {
            onClickButton()
        })

    }

    private fun setViews() {
        title = findViewById<TextView>(R.id.title)
        switch = findViewById<ToggleButton>(R.id.toggle_button)
        hEdit = findViewById<EditText>(R.id.height_edit)
        wEdit = findViewById<EditText>(R.id.width_edit)
        wText = findViewById<TextView>(R.id.width)
        hText = findViewById<TextView>(R.id.height)
        transCheck = findViewById<CheckBox>(R.id.transparent_check)
    }

    //高さ・幅の変更
    private fun setParams(position: String, height: Int = -1, width: Int = -1) {

        when (position) {
            KeyStore.TOP -> viewModel.setTopParam(height, width)
            KeyStore.BOTTOM -> viewModel.setBottomParam(height, width)
            KeyStore.RIGHT -> viewModel.setRightParam(height, width)
            else -> viewModel.setLeftParam(height, width)
        }
        //プリファレンス（設定）に保存
        getSharedPreferences(PREF.Name.key, MODE_PRIVATE).edit().apply {
            if (height > 0) putInt(position + "Height", height)
            if (width > 0) putInt(position + "Width", width)
            apply()
        }
    }

    //アイコンサイズの変更
    private fun changeIconSize(height: Int, width: Int, progress: Int) {
        val maxSize = min(height, width)
        val ratio = progress / 100f //(min)0 ~ 1(max)
        val size = ratio * maxSize
        viewModel.setTopParam(size = size.toInt())
    }

    //positionボタンクリック
    private fun onClickButton() {
        val pref = getSharedPreferences(PREF.Name.key, MODE_PRIVATE)
        when (viewModel.nowPos.value!!) {
            KeyStore.TOP -> {
                setValue(
                    KeyStore.TOP,
                    viewModel.topHeight.value.toString(),
                    viewModel.topWidth.value.toString()
                )
                switch.isChecked = pref.getBoolean(PREF.TopActive.key, false)
            }
            KeyStore.BOTTOM -> {
                setValue(
                    KeyStore.BOTTOM,
                    viewModel.bottomHeight.value.toString(),
                    viewModel.bottomWidth.value.toString()
                )
                switch.isChecked = pref.getBoolean(PREF.BottomActive.key, false)
            }
            KeyStore.RIGHT -> {
                setValue(
                    KeyStore.RIGHT,
                    viewModel.rightHeight.value.toString(),
                    viewModel.rightWidth.value.toString()
                )
                switch.isChecked = pref.getBoolean(PREF.RightActive.key, false)
            }
            else -> {
                setValue(
                    KeyStore.LEFT,
                    viewModel.leftHeight.value.toString(),
                    viewModel.leftWidth.value.toString()
                )
                switch.isChecked = pref.getBoolean(PREF.LeftActive.key, false)
            }
        }
    }

    private fun setValue(position: String, height: String, width: String) {
        title.text = position
        hEdit.setText(height)
        wEdit.setText(width)
        hText.text = height
        wText.text = width
    }

//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////アプリ初回起動時の処理//////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////

    private fun initialBoot() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val dWidth = dm.widthPixels
        val dHeight = dm.heightPixels
        setParams(KeyStore.TOP, height = 200, width = dWidth)
        setParams(KeyStore.BOTTOM, height = 200, width = dWidth)
        setParams(KeyStore.RIGHT, height = dHeight, width = 100)
        setParams(KeyStore.LEFT, height = dHeight, width = 100)
        getSharedPreferences(PREF.Name.key, MODE_PRIVATE).edit().apply {
            putLong(PREF.FirstDate.key, System.currentTimeMillis())
            apply()
        }
        Toast.makeText(this, "First", Toast.LENGTH_SHORT).show()
    }

    /** パーミッションリクエスト */
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

    /** パーミッションチェック */
    private fun isOverlayGranted() = Settings.canDrawOverlays(this)

}
