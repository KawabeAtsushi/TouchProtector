package com.pandatone.touchProtector.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandatone.touchProtector.R
import com.pandatone.touchProtector.ui.view.HomeFragment

class HomeViewModel : ViewModel() {

    //status
    private val _status = MutableLiveData<String>().also { mutableLiveData ->
        mutableLiveData.value = "status"
    }
    val status: LiveData<String>
        get() = _status

    fun setStatus(status: String) {
        _status.value = status
    }

    //now icon
    private val _nowIcon = MutableLiveData<Int>().also {
        it.value = R.drawable.ic_trans_circle
    }
    val nowIcon: LiveData<Int>
        get() = _nowIcon

    //Icon変更
    fun changeIcon(iconId: Int) {
        _nowIcon.value = iconId
        HomeFragment.iconDialog?.dismiss()
    }

    //now color
    private val _nowColor = MutableLiveData<Int>().also {
        it.value = R.color.yellow
    }
    val nowColor: LiveData<Int>
        get() = _nowColor

    //Color変更
    fun changeColor(colorId: Int) {
        _nowColor.value = colorId
        HomeFragment.colorDialog?.dismiss()
    }

    private val _iconSize = MutableLiveData<Int>().also {
        it.value = 0
    }
    val iconSize: LiveData<Int>
        get() = _iconSize

    fun setIconSize(size: Int) {
        _iconSize.value = size
    }
}