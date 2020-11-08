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
    IconSize("iconSize"),
    TopH("topHeight"),
    TopW("topWidth"),
    TopVisible("topVisible"),
    BottomH("bottomHeight"),
    BottomW("bottomWidth"),
    BottomVisible("bottomVisible"),
    LeftH("leftHeight"),
    LeftW("leftWidth"),
    LeftVisible("leftVisible"),
    RightH("rightHeight"),
    RightW("rightWidth"),
    RightVisible("rightVisible")
}
