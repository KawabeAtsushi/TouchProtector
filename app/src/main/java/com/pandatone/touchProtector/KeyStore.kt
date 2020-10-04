package com.pandatone.touchProtector

import android.util.Log

object KeyStore {
    const val TOP = "Top"
    const val BOTTOM = "Bottom"
    const val LEFT = "Left"
    const val RIGHT = "Right"
}

//プリファレンスキー
enum class PREF(val key: String) {
    Name("my_settings"),
    FirstDate("first_date"),
    TopH("TopHeight"),
    TopW("TopWidth"),
    BottomH("BottomHeight"),
    BottomW("BottomWidth"),
    LeftH("LeftHeight"),
    LeftW("LeftWidth"),
    RightH("RightHeight"),
    RightW("RightWidth")
}
