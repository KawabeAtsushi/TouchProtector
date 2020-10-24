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
    TopH("topHeight"),
    TopW("topWidth"),
    TopActive("topActive"),
    BottomH("bottomHeight"),
    BottomW("bottomWidth"),
    BottomActive("bottomActive"),
    LeftH("leftHeight"),
    LeftW("leftWidth"),
    LeftActive("leftActive"),
    RightH("rightHeight"),
    RightW("rightWidth"),
    RightActive("rightActive")
}
