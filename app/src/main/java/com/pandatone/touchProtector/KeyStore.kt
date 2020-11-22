package com.pandatone.touchProtector

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
    IconSize("iconSize"),
    IconId("iconId"),
    ColorId("colorId"),
    TopH("TopHeight"),
    TopW("TopWidth"),
    TopVisible("TopVisible"),
    BottomH("BottomHeight"),
    BottomW("BottomWidth"),
    BottomVisible("BottomVisible"),
    LeftH("LeftHeight"),
    LeftW("LeftWidth"),
    LeftVisible("LeftVisible"),
    RightH("RightHeight"),
    RightW("RightWidth"),
    RightVisible("RightVisible")
}
