package com.pandatone.touchProtector.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.pandatone.touchProtector.PREF
import com.pandatone.touchProtector.PurchaseUnlimited
import com.pandatone.touchProtector.R
import com.pandatone.touchProtector.ui.view.HomeFragment


/**
 * Created by atsushi_2 on 2016/11/11.
 */

class UpgradeDialog : DialogFragment() {

    private lateinit var dimmer: View
    private lateinit var rewardedAd: RewardedAd
    private var adId = "ca-app-pub-2315101868638564/8255088916"
    private var testDevice = "2295CE3E4BC9C5CFD1F0B805F5E8846A"
    private var rewarded = false
    private lateinit var loadingAnim: LottieAnimationView

    override fun onCreateDialog(savedInstanceState: Bundle?): AppCompatDialog {

        MobileAds.initialize(activity)
        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf(testDevice))
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)

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
        }
        // 広告視聴ボタンのリスナ
        watchAdButton.setOnClickListener {
            loadAd()
            dimmer = dialog.findViewById(R.id.dimmer_layout)!!
            dimmer.visibility = View.VISIBLE
            loadingAnim = dialog.findViewById(R.id.loading_anim)!!
            loadingAnim.visibility = View.VISIBLE
            loadingAnim.playAnimation()}

        return dialog
    }

    private fun loadAd() {

        rewardedAd = RewardedAd(activity, adId)

        val adLoadCallback: RewardedAdLoadCallback = object : RewardedAdLoadCallback() {

            // 広告の準備が完了したとき
            override fun onRewardedAdLoaded() {
                showAd()
                loadingAnim.visibility = View.GONE
                loadingAnim.cancelAnimation()
            }

            override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                Log.d("AdLog", "The rewarded ad was failed to Load. : $adError")
            }
        }
        rewardedAd.loadAd(
            AdRequest.Builder().build(),
            adLoadCallback
        )
    }

    fun showAd() {

        val adCallback: RewardedAdCallback = object : RewardedAdCallback() {

            //報酬対象になったとき
            override fun onUserEarnedReward(p0: RewardItem) {
                rewarded = true
            }

            //広告が閉じられたとき
            override fun onRewardedAdClosed() {
                super.onRewardedAdClosed()
                dimmer.visibility = View.GONE

                if (rewarded) {
                    activity!!.getSharedPreferences(PREF.Name.key, AppCompatActivity.MODE_PRIVATE)
                        .edit()
                        .apply {
                            putLong(
                                PREF.FirstDate.key,
                                System.currentTimeMillis() - (168 - 24) * 3600 * 1000
                            )
                            apply()
                        }
                    val status =
                        activity?.getString(R.string.status_trial) + "24" + activity?.getString(R.string.hours)
                    HomeFragment.viewModel.setStatus(status)
                    Toast.makeText(
                        activity,
                        activity?.getString(R.string.rewarded_ad),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    rewarded = false
                    dialog?.dismiss()
                }
            }

        }

        //広告を表示
        rewardedAd.show(activity, adCallback)

    }

}