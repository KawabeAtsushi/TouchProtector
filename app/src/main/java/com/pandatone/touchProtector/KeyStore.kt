package com.pandatone.touchProtector

import android.util.Log

object KeyStore {
    const val TOP = "top"
    const val BOTTOM = "bottom"
    const val LEFT = "left"
    const val RIGHT = "right"
}

//プリファレンスキー
enum class PREF(val key: String) {
    Name("my_settings"),
    FirstDate("first_date"),
    TopH("topHeight"),
    TopW("topWidth"),
    BottomH("bottomHeight"),
    BottomW("bottomWidth"),
    LeftH("leftHeight"),
    LeftW("leftWidth"),
    RightH("rightHeight"),
    RightW("rightWidth")
}
