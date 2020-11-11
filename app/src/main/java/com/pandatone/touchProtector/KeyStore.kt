package com.pandatone.touchProtector

import android.util.Log

object KeyStore {
    const val TOP = "top"
    const val BOTTOM = "bottom"
    const val LEFT = "left"
    const val RIGHT = "right"

    //status
    const val TRIAL = "trial"
    const val EXPIRED = "expired"
    const val UNLIMITED = "unlimited"
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

//アイコン名
enum class ICON(val key: String) {
    None("None"),
    Block("block"),
    Close("close"),
    Cat("cat"),
    Droid("droid"),
    Favorite("favorite"),
    Hand("hand"),
    Lock("lock"),
    Security("security"),
    Star("star")
}

//カラー名
enum class COLOR(val key: String) {
    None("None"),
    Block("block"),
    Close("close"),
    Cat("cat"),
    Droid("droid"),
    Favorite("favorite"),
    Hand("hand"),
    Lock("lock"),
    Security("security"),
    Star("star")
}
