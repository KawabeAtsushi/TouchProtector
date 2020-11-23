package com.pandatone.touchProtector.ui.dialog

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.pandatone.touchProtector.PREF
import com.pandatone.touchProtector.PurchaseUnlimited
import com.pandatone.touchProtector.R
import com.pandatone.touchProtector.ui.view.HomeFragment

/**
 * Created by atsushi_2 on 2016/11/11.
 */

class UpgradeDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): AppCompatDialog {

        HomeFragment.viewModel.setStatus(getString(R.string.status_expired))

        val dialog = activity?.let { AppCompatDialog(it) }
        // タイトル非表示
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        // フルスクリーン
        dialog.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        dialog.setContentView(R.layout.upgrade_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.isCancelable = false
        dialog.setCanceledOnTouchOutside(false)

        // ボタンの設定
        val upgradeButton = dialog.findViewById<Button>(R.id.upgrade_button)!!
        val watchAdButton = dialog.findViewById<Button>(R.id.reward_button)!!
        // アップグレードボタンのリスナ
        upgradeButton.setOnClickListener {
            PurchaseUnlimited(activity!!)
            HomeFragment.viewModel.setStatus(getString(R.string.status_unlimited))
        }
        // 広告視聴ボタンのリスナ
        watchAdButton.setOnClickListener { AdMovie(activity!!,dialog) }

        return dialog
    }

    class AdMovie(private val activity: Activity, private val dialog: AppCompatDialog) :
        RewardedVideoAdListener {

        private lateinit var mRewardedVideoAd: RewardedVideoAd
        var adId = "ca-app-pub-2315101868638564/8255088916"
        var testDeviceId = "zte-901zt-320496917125"
        var rewarded = false

        init {
            oneDayExtension()
        }

        private fun oneDayExtension() {

            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity)
            mRewardedVideoAd.rewardedVideoAdListener = this
            mRewardedVideoAd.loadAd(
                adId,
                AdRequest.Builder().addTestDevice(testDeviceId).build()
            )
        }

        ////////////////////リワード広告のオーバーライド////////////////////////////////////////////

        // 広告の準備が完了したとき
        override fun onRewardedVideoAdLoaded() {
            mRewardedVideoAd.show()
        }

        //報酬対象になったとき
        override fun onRewarded(p0: com.google.android.gms.ads.reward.RewardItem?) {
            rewarded = true
        }

        //広告が閉じられたとき
        override fun onRewardedVideoAdClosed() {
            activity.getSharedPreferences(PREF.Name.key, AppCompatActivity.MODE_PRIVATE).edit()
                .apply {
                    putLong(PREF.FirstDate.key, System.currentTimeMillis() - 3600 * 1000)
                    apply()
                }
            val status = activity.getString(R.string.status_trial) + "24" +
                    activity.getString(R.string.hours)
            HomeFragment.viewModel.setStatus(status)
            if (rewarded) {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.rewarded_ad),
                    Toast.LENGTH_LONG
                )
                    .show()
                rewarded = false
                dialog.dismiss()
            }
        }

        override fun onRewardedVideoAdOpened() {}
        override fun onRewardedVideoStarted() {}
        override fun onRewardedVideoAdLeftApplication() {}
        override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {}
        override fun onRewardedVideoCompleted() {}
    }

}