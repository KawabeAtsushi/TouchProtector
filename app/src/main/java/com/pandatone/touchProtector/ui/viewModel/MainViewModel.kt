package com.pandatone.touchProtector.ui.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandatone.touchProtector.ICON
import com.pandatone.touchProtector.KeyStore
import com.pandatone.touchProtector.R

class MainViewModel : ViewModel() {

    //status
    private val _status = MutableLiveData<String>().also { mutableLiveData ->
        mutableLiveData.value = KeyStore.TRIAL
    }
    val status: LiveData<String>
        get() = _status

    fun setStatus(status: String,context: Context) {
        _status.value = when(status){
            KeyStore.TRIAL -> context.getString(R.string.status_trial)
            KeyStore.EXPIRED -> context.getString(R.string.status_expired)
            else -> context.getString(R.string.status_unlimited)
        }
    }

    //now icon
    private val _nowIcon = MutableLiveData<Int>().also { mutableLiveData ->
        mutableLiveData.value = R.drawable.ic_block
    }
    val nowIcon: LiveData<Int>
        get() = _nowIcon

    //Icon変更
    fun changeIcon(iconName: String) {
        _nowIcon.value = when (iconName) {
            ICON.None.key ->  0
            ICON.Block.key -> R.drawable.ic_block
            ICON.Close.key -> R.drawable.ic_close
            ICON.Lock.key -> R.drawable.ic_lock
            ICON.Star.key -> R.drawable.ic_star
            ICON.Favorite.key -> R.drawable.ic_favorite
            ICON.Security.key -> R.drawable.ic_security
            ICON.Hand.key -> R.drawable.ic_hand
            ICON.Cat.key -> R.drawable.ic_cat
            else -> R.drawable.ic_droid
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