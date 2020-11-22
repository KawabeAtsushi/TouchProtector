package com.pandatone.touchProtector.ui.viewModel

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandatone.touchProtector.MainActivity
import com.pandatone.touchProtector.PREF
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
        it.value = R.drawable.ic_block
    }
    val nowIcon: LiveData<Int>
        get() = _nowIcon

    //Icon変更
    fun changeIcon(context: Context,iconId: Int) {
        _nowIcon.value = iconId
        context.getSharedPreferences(PREF.Name.key, AppCompatActivity.MODE_PRIVATE).edit().apply {
            putInt(PREF.IconId.key, iconId)
            apply()
        }
        HomeFragment.iconDialog?.dismiss()
    }

    //now color
    private val _nowColor = MutableLiveData<Int>().also {
        it.value = R.color.yellow
    }
    val nowColor: LiveData<Int>
        get() = _nowColor

    //Color変更
    fun changeColor(context: Context,colorId: Int) {
        _nowColor.value = colorId
        context.getSharedPreferences(PREF.Name.key, AppCompatActivity.MODE_PRIVATE).edit().apply {
            putInt(PREF.ColorId.key, colorId)
            apply()
        }
        HomeFragment.colorDialog?.dismiss()
    }

    private val _iconSize = MutableLiveData<Int>().also {
        it.value = 100
    }
    val iconSize: LiveData<Int>
        get() = _iconSize

    fun setIconSize(size: Int) {
        _iconSize.value = size
    }
}