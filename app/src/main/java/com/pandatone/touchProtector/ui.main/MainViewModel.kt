package com.pandatone.touchProtector.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandatone.touchProtector.KeyStore

class MainViewModel : ViewModel() {

    val nowHeight: Int
        get() {
            return when (nowPos.value) {
                KeyStore.TOP -> topHeight.value ?: 0
                KeyStore.BOTTOM -> bottomHeight.value ?: 0
                KeyStore.RIGHT -> rightHeight.value ?: 0
                else -> leftHeight.value ?: 0
            }
        }

    val nowWidth: Int
        get() {
            return when (nowPos.value) {
                KeyStore.TOP -> topWidth.value ?: 0
                KeyStore.BOTTOM -> bottomWidth.value ?: 0
                KeyStore.RIGHT -> rightWidth.value ?: 0
                else -> leftWidth.value ?: 0
            }
        }

    val nowVisible: Boolean
        get() {
            return when (nowPos.value) {
                KeyStore.TOP -> topVisible.value ?: true
                KeyStore.BOTTOM -> bottomVisible.value ?: true
                KeyStore.RIGHT -> rightVisible.value ?: true
                else -> leftVisible.value ?: true
            }
        }

    //TOP
    private val _topVisible = MutableLiveData<Boolean>().also { mutableLiveData ->
        mutableLiveData.value = true
    }
    val topVisible: LiveData<Boolean>
        get() = _topVisible

    fun setTopVisible(visible: Boolean) {
        _topVisible.value = visible
    }

    private val _topHeight = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = 200
    }
    val topHeight: LiveData<Int>
        get() = _topHeight

    private val _topWidth = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = 200
    }
    val topWidth: LiveData<Int>
        get() = _topWidth

    fun setTopParam(height: Int = -1, width: Int = -1) {
        if (height > 0) _topHeight.value = height
        if (width > 0) _topWidth.value = width
    }

    //BOTTOM
    private val _bottomVisible = MutableLiveData<Boolean>().also { mutableLiveData ->
        mutableLiveData.value = true
    }
    val bottomVisible: LiveData<Boolean>
        get() = _bottomVisible

    fun setBottomVisible(visible: Boolean) {
        _bottomVisible.value = visible
    }

    private val _bottomHeight = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = 200
    }
    val bottomHeight: LiveData<Int>
        get() = _bottomHeight

    private val _bottomWidth = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = 200
    }
    val bottomWidth: LiveData<Int>
        get() = _bottomWidth

    fun setBottomParam(height: Int = -1, width: Int = -1) {
        if (height > 0) _bottomHeight.value = height
        if (width > 0) _bottomWidth.value = width
    }

    //RIGHT
    private val _rightVisible = MutableLiveData<Boolean>().also { mutableLiveData ->
        mutableLiveData.value = true
    }
    val rightVisible: LiveData<Boolean>
        get() = _rightVisible

    fun setRightVisible(visible: Boolean) {
        _rightVisible.value = visible
    }

    private val _rightHeight = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = 200
    }
    val rightHeight: LiveData<Int>
        get() = _rightHeight

    private val _rightWidth = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = 200
    }
    val rightWidth: LiveData<Int>
        get() = _rightWidth

    fun setRightParam(height: Int = -1, width: Int = -1) {
        if (height > 0) _rightHeight.value = height
        if (width > 0) _rightWidth.value = width
    }

    //LEFT
    private val _leftVisible = MutableLiveData<Boolean>().also { mutableLiveData ->
        mutableLiveData.value = true
    }
    val leftVisible: LiveData<Boolean>
        get() = _leftVisible

    fun setLeftVisible(visible: Boolean) {
        _leftVisible.value = visible
    }

    private val _leftHeight = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = 200
    }
    val leftHeight: LiveData<Int>
        get() = _leftHeight

    private val _leftWidth = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = 200
    }
    val leftWidth: LiveData<Int>
        get() = _leftWidth

    fun setLeftParam(height: Int = -1, width: Int = -1) {
        if (height > 0) _leftHeight.value = height
        if (width > 0) _leftWidth.value = width
    }

    //now position
    private val _nowPos = MutableLiveData<String>().also { mutableLiveData ->
        mutableLiveData.value = KeyStore.TOP
    }
    val nowPos: LiveData<String>
        get() = _nowPos

    //positionボタンクリック
    fun changePosition(positionNo: Int = 0) {
        when (positionNo) {
            0 -> _nowPos.value = KeyStore.TOP
            1 -> _nowPos.value = KeyStore.RIGHT
            2 -> _nowPos.value = KeyStore.BOTTOM
            else -> _nowPos.value = KeyStore.LEFT
        }
    }

    private val _iconSize = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = 0
    }
    val iconSize: LiveData<Int>
        get() = _iconSize

    fun setIconSize(size: Int) {
        _iconSize.value = size
    }
}