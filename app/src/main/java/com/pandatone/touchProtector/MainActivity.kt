package com.pandatone.touchProtector

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.pandatone.touchProtector.ui.main.MainViewModel
import com.pandatone.touchProtector.ui.main.SectionsPagerAdapter


/**
 * オーバーレイ表示を開始／終了するためのトグルボタンを表示します。
 */
class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var viewModel: MainViewModel

        var dWidth = 100
        var dHeight = 100
        var statusText = "No Data"
        /** ID for the runtime permission dialog */
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setContentView(R.layout.main_activity)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        requestOverlayPermission()

        getDisplaySize()

        val pref = getSharedPreferences(PREF.Name.key, MODE_PRIVATE)
        val firstDate = pref.getLong(PREF.FirstDate.key, 0)
        if (firstDate == 0L) {
            initialBoot()
        } else {
            viewModel.setTopVisible(pref.getBoolean(PREF.TopVisible.key, true))
            viewModel.setBottomVisible(pref.getBoolean(PREF.BottomVisible.key, true))
            viewModel.setRightVisible(pref.getBoolean(PREF.RightVisible.key, true))
            viewModel.setLeftVisible(pref.getBoolean(PREF.LeftVisible.key, true))
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
            viewModel.setIconSize(pref.getInt(PREF.IconSize.key, 100))
        }

        val lastDay = System.currentTimeMillis() - pref.getLong(PREF.FirstDate.key, 0)
        val limit = 72 * 3600 * 1000
        statusText = if (lastDay > limit) {
            limitDialog()
            getString(R.string.status_unlimited)
        } else {
            val minutes = (limit - lastDay) / 60000
            getString(R.string.status_trial) + (minutes / 60).toString() +
                    getString(R.string.hours) + (minutes % 60).toString() + getString(R.string.mins)
        }

    }

    private fun getDisplaySize() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
        dWidth = dm.widthPixels
        dHeight = dm.heightPixels
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

//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////アプリ初回起動時の処理//////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////

    private fun initialBoot() {
        setParams(KeyStore.TOP, height = 200, width = dHeight)
        setParams(KeyStore.BOTTOM, height = 200, width = dHeight)
        setParams(KeyStore.RIGHT, height = dHeight, width = 100)
        setParams(KeyStore.LEFT, height = dHeight, width = 100)
        getSharedPreferences(PREF.Name.key, MODE_PRIVATE).edit().apply {
            putLong(PREF.FirstDate.key, System.currentTimeMillis())
            apply()
        }
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
