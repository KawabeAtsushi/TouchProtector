package com.pandatone.touchProtector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pandatone.touchProtector.ui.overlay.OverlayService

class StopServiceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        OverlayService.stop(context)
    }
}