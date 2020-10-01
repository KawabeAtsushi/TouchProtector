package com.pandatone.touchProtector

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel()  {

    val top = "top"
    val bottom = "bottom"
    val left = "left"
    val right = "right"

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

    //onClick
    fun onClickButton(title: TextView, position:String){

        when (position) {
            top -> title.text = top
            bottom -> title.text = bottom
            right -> title.text = right
            else -> title.text = left
        }
    }

}