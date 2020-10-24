package com.pandatone.touchProtector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    //TOP
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

    private val _topSize = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = 0
    }
    val topSize: LiveData<Int>
        get() = _topSize

    fun setTopParam(height: Int = -1, width: Int = -1, size: Int = -1) {
        if (height > 0) _topHeight.value = height
        if (width > 0) _topWidth.value = width
        if (size > 0) _topSize.value = size
    }

    //BOTTOM
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
    fun changePosition(positionNo: Int) {
        when (positionNo) {
            0 -> _nowPos.value = KeyStore.TOP
            1 -> _nowPos.value = KeyStore.RIGHT
            2 -> _nowPos.value = KeyStore.BOTTOM
            else -> _nowPos.value = KeyStore.LEFT
        }
    }

    private fun onClick(
        position: String,
        title: TextView,
        height: LiveData<Int>,
        width: LiveData<Int>,
        heightTv: TextView,
        widthTv: TextView,
        heightEt: EditText,
        widthEt: EditText
    ) {
        StatusHolder.nowPos = position
        MainActivity.nowHeight = height
        MainActivity.nowWidth = width
        title.text = position
        heightTv.text = height.value.toString()
        widthTv.text = width.value.toString()
        heightEt.setText(height.value.toString())
        widthEt.setText(width.value.toString())
    }

}