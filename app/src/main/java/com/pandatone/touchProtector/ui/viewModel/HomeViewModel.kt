package com.pandatone.touchProtector.ui.viewModel

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    private val _nowIcon = MutableLiveData<Drawable>().also { mutableLiveData ->
        mutableLiveData.value = null
    }
    val nowIcon: LiveData<Drawable>
        get() = _nowIcon

    //Icon変更
    fun changeIcon(iconDrawable: Drawable) {
        Log.d("icon",iconDrawable.toString())
        _nowIcon.value = iconDrawable
        HomeFragment.iconDialog?.dismiss()
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