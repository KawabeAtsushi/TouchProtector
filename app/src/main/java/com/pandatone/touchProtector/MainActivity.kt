package com.pandatone.touchProtector

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.pandatone.touchProtector.databinding.MainActivityBinding
import kotlin.math.min


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
        lateinit var nowHeight: LiveData<Int>
        lateinit var nowWidth: LiveData<Int>

        /** ID for the runtime permission dialog */
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: MainActivityBinding = MainActivityBinding.inflate(layoutInflater)
        viewModel = MainViewModel()
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
            viewModel.setBottomParam(
                pref.getInt(PREF.BottomH.key, 0),
                pref.getInt(PREF.BottomW.key, 0)
            )
            viewModel.setRightParam(
                pref.getInt(PREF.RightH.key, 0),
                pref.getInt(PREF.RightW.key, 0)
            )
            viewModel.setLeftParam(pref.getInt(PREF.LeftH.key, 0), pref.getInt(PREF.LeftW.key, 0))
        }

        nowHeight = viewModel.topHeight
        nowWidth = viewModel.topWidth

        changeIconSize(pref.getInt("topHeight", 0), pref.getInt("topWidth", 0), 100)

        val statusView = findViewById<TextView>(R.id.status)
        val lastDay = System.currentTimeMillis() - pref.getLong(PREF.FirstDate.key, 0)
        val limit = 72 * 3600 * 1000
        if (lastDay > limit) {
            limitDialog()
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
                update()
                if (isChecked) OverlayService.start(this@MainActivity)
                else OverlayService.stop(this@MainActivity)
            }
        }

        findViewById<ImageButton>(R.id.refresh).setOnClickListener {
            update()
            if (OverlayService.isActive) OverlayService.refresh(this@MainActivity)
        }

        findViewById<SeekBar>(R.id.seekBar).setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                //ツマミがドラッグされると呼ばれる
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    changeIconSize(
                        nowHeight.value ?: 0,
                        nowWidth.value ?: 0,
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
    }

    private fun setViews() {
        heightEt = findViewById<EditText>(R.id.height_edit)
        widthEt = findViewById<EditText>(R.id.width_edit)
        widthTv = findViewById<TextView>(R.id.width)
        heightTv = findViewById<TextView>(R.id.height)
        transCheck = findViewById<CheckBox>(R.id.transparent_check)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
        val dWidth = dm.widthPixels.toString()
        val dHeight = dm.heightPixels.toString()
        val displaySize = findViewById<TextView>(R.id.display_size)
        displaySize.text = getString(R.string.disp_size) + "$dWidth × $dHeight"
    }

    //アプリ初回起動時の処理
    private fun initialBoot() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
        val dHeight = dm.heightPixels
        setParams(KeyStore.TOP, height = 200, width = dHeight)
        setParams(KeyStore.BOTTOM, height = 200, width = dHeight)
        setParams(KeyStore.RIGHT, height = dHeight, width = 100)
        setParams(KeyStore.LEFT, height = dHeight, width = 100)
        getSharedPreferences(PREF.Name.key, MODE_PRIVATE).edit().apply {
            putLong(PREF.FirstDate.key, System.currentTimeMillis())
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

    //期限が来たら出す
    private fun limitDialog() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.limit_title)
            .setMessage(R.string.limit_text)
            .setPositiveButton(R.string.upgrade, { dialog, which ->
                // TODO:Yesが押された時の挙動
            })
            .show()
    }

    private fun update() {
        val heightStr = heightEt.text.toString()
        if (heightStr != "") {
            setParams(StatusHolder.nowPos, height = Integer.parseInt(heightStr))
        }
        val widthStr = widthEt.text.toString()
        if (widthStr != "") {
            setParams(StatusHolder.nowPos, width = Integer.parseInt(widthStr))
        }
        OverlayService.transBackground = transCheck.isChecked
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        changeIconSize(
            nowHeight.value ?: 0,
            nowWidth.value ?: 0,
            seekBar.progress
        )
    }

    private fun changeIconSize(height: Int, width: Int, progress: Int) {
        val maxSize = min(height, width)
        val ratio = progress / 100f //(min)0 ~ 1(max)
        val size = ratio * maxSize
        viewModel.setTopParam(size = size.toInt())
    }
}
