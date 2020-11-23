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
import com.pandatone.touchProtector.ui.view.SectionsPagerAdapter
import com.pandatone.touchProtector.ui.view.HomeFragment
import com.pandatone.touchProtector.ui.view.SettingFragment
import com.pandatone.touchProtector.ui.viewModel.HomeViewModel
import com.pandatone.touchProtector.ui.viewModel.SettingViewModel


/**
 * オーバーレイ表示を開始／終了するためのトグルボタンを表示します。
 */
class MainActivity : AppCompatActivity() {

    companion object {

        var dWidth = 100
        var dHeight = 100
        var statusText = "No Data"

        /** ID for the runtime permission dialog */
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeFragment.viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        SettingFragment.viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        setContentView(R.layout.main_activity)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.currentItem = 1
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        requestOverlayPermission()

        getDisplaySize()

        val pref = getSharedPreferences(PREF.Name.key, MODE_PRIVATE)
        val firstDate = pref.getLong(PREF.FirstDate.key, 0)
        if (firstDate == 0L) {
            initialBoot()
        } else {
            val homeViewModel = HomeFragment.viewModel
            homeViewModel.changeIcon(this,pref.getInt(PREF.IconId.key, R.drawable.ic_block))
            homeViewModel.changeColor(this,pref.getInt(PREF.ColorId.key, R.color.yellow))
            val settingViewModel = SettingFragment.viewModel
            settingViewModel.setTopVisible(pref.getBoolean(PREF.TopVisible.key, true))
            settingViewModel.setBottomVisible(pref.getBoolean(PREF.BottomVisible.key, true))
            settingViewModel.setRightVisible(pref.getBoolean(PREF.RightVisible.key, true))
            settingViewModel.setLeftVisible(pref.getBoolean(PREF.LeftVisible.key, true))
            settingViewModel.setTopParam(pref.getInt(PREF.TopH.key, 0), pref.getInt(PREF.TopW.key, 0))
            settingViewModel.setBottomParam(
                pref.getInt(PREF.BottomH.key, 0),
                pref.getInt(PREF.BottomW.key, 0)
            )
            settingViewModel.setRightParam(
                pref.getInt(PREF.RightH.key, 0),
                pref.getInt(PREF.RightW.key, 0)
            )
            settingViewModel.setLeftParam(pref.getInt(PREF.LeftH.key, 0), pref.getInt(PREF.LeftW.key, 0))
            HomeFragment.viewModel.setIconSize(pref.getInt(PREF.IconSize.key, 100))
        }

        val lastDay = System.currentTimeMillis() - pref.getLong(PREF.FirstDate.key, 0)
        val limit = 168 * 3600 * 1000
        statusText = if (lastDay > limit) {
            limitDialog()
            getString(R.string.status_unlimited)
        } else {
            val minutes = (limit - lastDay) / 60000
            getString(R.string.status_trial) + (minutes / 60).toString() +
                    getString(R.string.hours) + (minutes % 60).toString() + getString(R.string.mins)
        }

        HomeFragment.viewModel.setStatus(statusText)
    }

    private fun getDisplaySize() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
        dWidth = dm.widthPixels
        dHeight = dm.heightPixels
    }

    //高さ・幅の変更
    private fun setParams(position: String, height: Int = -1, width: Int = -1) {

        val settingViewModel = SettingFragment.viewModel

        when (position) {
            KeyStore.TOP -> settingViewModel.setTopParam(height, width)
            KeyStore.BOTTOM -> settingViewModel.setBottomParam(height, width)
            KeyStore.RIGHT -> settingViewModel.setRightParam(height, width)
            else -> settingViewModel.setLeftParam(height, width)
        }
        //プリファレンス（設定）に保存
        getSharedPreferences(PREF.Name.key, MODE_PRIVATE).edit().apply {
            if (height > 0) putInt(position + "Height", height)
            if (width > 0) putInt(position + "Width", width)
            apply()
        }
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

//////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////更新時の処理////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////

    //期限が来たら出す
    private fun limitDialog() {
        HomeFragment.viewModel.setStatus(getString(R.string.status_expired))

        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.limit_title)
            .setMessage(R.string.limit_text)
            .setPositiveButton(R.string.upgrade) { _, _ ->
                // TODO:Yesが押された時の挙動
                HomeFragment.viewModel.setStatus(getString(R.string.status_unlimited))
            }
            .show()
    }
}
