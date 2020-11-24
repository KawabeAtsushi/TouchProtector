package com.pandatone.touchProtector

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.pandatone.touchProtector.ui.view.HomeFragment


/*
querySkuList() //問い合わせた4つのアイテムの情報を表示する
queryOwned() //購入済みのアイテムを表示する。
startPurchase("android.test.purchased") //「android.test.purchased」を購入するためのダイアログを表示する。
queryPurchaseHistory()
 */


class PurchaseUnlimited(private val activity: Activity) : PurchasesUpdatedListener,
    AcknowledgePurchaseResponseListener {

    companion object {
        var billingClient: BillingClient? = null
    }
    private var mySkuDetailsList: List<SkuDetails>? = null

    init {
        // BillingClientを準備する
        billingClient = BillingClient.newBuilder(activity)
            .setListener(this).enablePendingPurchases().build()
        if (KeyStore.purchaseCheck) {
            statusCheck() //アップグレード済かチェック（statusを更新）
            KeyStore.purchaseCheck = false
        } else {
            billingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    val responseCode = billingResult.responseCode
                    if (responseCode == BillingClient.BillingResponseCode.OK) {
                        querySkuList() //sku初期化から購入処理
                    } else {
                        showResponseCode(responseCode)
                    }
                }

                override fun onBillingServiceDisconnected() { // Try to restart the connection on the next request to
                }
            })
        }
    }

    // skuの初期化
    private fun querySkuList() {
        val skuList = ArrayList<String>()
        skuList.add(KeyStore.unlimited_sku)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient!!.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList ->
            val responseCode = billingResult.responseCode
            if (responseCode == BillingClient.BillingResponseCode.OK) { // 後の購入手続きのためにSkuの詳細を保持
                mySkuDetailsList = skuDetailsList
                startPurchase(KeyStore.unlimited_sku)  //購入
            } else {
                showResponseCode(responseCode)
            }
        }
    }

    // 購入処理を開始する
    private fun startPurchase(sku: String) {
        val skuDetails = getSkuDetails(sku)
        if (skuDetails != null) {
            val params = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
            val billingResult = billingClient!!.launchBillingFlow(activity, params)
            showResponseCode(billingResult.responseCode)
        }
    }

    // 指定したSKUの詳細をリスト内から得る
    private fun getSkuDetails(sku: String): SkuDetails? {
        var skuDetails: SkuDetails? = null
        if (mySkuDetailsList == null) {
            toastShow("DetailList null")
        } else {
            for (sd in mySkuDetailsList!!) {
                if (sd.sku == sku) skuDetails = sd
            }
            if (skuDetails == null) {
                toastShow("Not match sku")
            }
        }
        return skuDetails
    }

    // 購入結果の更新時に呼ばれる
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        val resultStr = StringBuffer("")
        val billingResultCode = billingResult.responseCode
        if (billingResultCode == BillingClient.BillingResponseCode.OK
            && purchases != null
        ) {
            for (purchase in purchases) { //購入したら呼ばれる
                resultStr.append(skuToName(purchase.sku))
                unlimited()
            }
            toastShow(resultStr.toString())
        } else { // Handle error codes.
            showResponseCode(billingResultCode)
        }
    }

    // 購入承認の結果が戻る
    override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        KeyStore.purchaseCheck = true
        if (responseCode != BillingClient.BillingResponseCode.OK) {
            showResponseCode(responseCode)
        }
    }

    //無制限版は購入済みか？
    private fun statusCheck() {
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                val responseCodeB = billingResult.responseCode
                if (responseCodeB == BillingClient.BillingResponseCode.OK) {
                    val purchasesResult =
                        billingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
                    val responseCodeP = purchasesResult.responseCode
                    if (responseCodeP == BillingClient.BillingResponseCode.OK) {
                        val purchases = purchasesResult.purchasesList
                        for (purchase in purchases!!) {
                            if (purchase.sku == KeyStore.unlimited_sku) {
                                unlimited()
                            } //アップグレード済みか判定
                        }
                    } else {
                        showResponseCode(responseCodeP)
                    }
                } else {
                    showResponseCode(responseCodeB)
                }
            }

            override fun onBillingServiceDisconnected() { // Try to restart the connection on the next request to
            }
        })
    }

    //skuを商品名に変換
    private fun skuToName(sku: String): String {
        when (sku) {
            KeyStore.unlimited_sku -> return activity.getString(R.string.upgrade)
        }
        return "unknown"
    }

    // サーバの応答を表示する
    fun showResponseCode(responseCode: Int) {
        when (responseCode) {
            BillingClient.BillingResponseCode.USER_CANCELED -> toastShow("Cancel purchase")
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> toastShow("SERVICE_UNAVAILABLE")
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> toastShow("BILLING_UNAVAILABLE")
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> toastShow("ITEM_UNAVAILABLE")
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> toastShow("DEVELOPER_ERROR")
            BillingClient.BillingResponseCode.ERROR -> toastShow("ERROR")
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> toastShow("Unlimited already")
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> toastShow("ITEM_NOT_OWNED")
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> toastShow("SERVICE_DISCONNECTED")
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> toastShow("FEATURE_NOT_SUPPORTED")
        }
    }

    //トースト表示
    private fun toastShow(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }

    //無制限版にアップグレード
    private fun unlimited() {
        KeyStore.unlimited = true
        HomeFragment.viewModel.setStatus(activity.getString(R.string.status_unlimited))
    }
}