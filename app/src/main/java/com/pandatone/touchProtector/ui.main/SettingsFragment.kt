package com.pandatone.touchProtector.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pandatone.touchProtector.*
import com.pandatone.touchProtector.databinding.FragmentSettingsBinding

/**
 * A placeholder fragment containing a simple view.
 */
class SettingsFragment : Fragment() {

    private lateinit var title: TextView
    private lateinit var switch: SwitchCompat
    private lateinit var hEdit: EditText
    private lateinit var wEdit: EditText
    private lateinit var wText: TextView
    private lateinit var hText: TextView

    companion object {
        var allowChangeVisible = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.viewModel = MainActivity.viewModel
        // ここでMainActivity.viewModelから流れてきた値を受け取る.
        MainActivity.viewModel.nowPos.observe(viewLifecycleOwner, Observer {
            if (OverlayService.needChangeViews) {
                onChangePosition()
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setViews(view)

        // Show/Hideの切り替え
        switch.apply {
            setOnCheckedChangeListener { _, isChecked ->
                val viewModel = MainActivity.viewModel
                hText.text = viewModel.nowHeight.toString()
                wText.text = viewModel.nowWidth.toString()
                if (allowChangeVisible) {
                    setVisible(MainActivity.viewModel.nowPos.value!!, isChecked)
                    if (OverlayService.isActive) {
                        if (isChecked) OverlayService.show(context)
                        else OverlayService.hide(context)
                    }
                }
            }
        }
    }

    private fun setViews(view: View) {
        title = view.findViewById<TextView>(R.id.title)
        switch = view.findViewById<SwitchCompat>(R.id.active_switch)
        hEdit = view.findViewById<EditText>(R.id.height_edit)
        hEdit.doAfterTextChanged {
            val heightStr = it.toString()
            if (heightStr != "") {
                setParams(
                    MainActivity.viewModel.nowPos.value!!,
                    height = Integer.parseInt(heightStr)
                )
            }
        }
        wEdit = view.findViewById<EditText>(R.id.width_edit)
        wEdit.doAfterTextChanged {
            val widthStr = it.toString()
            if (widthStr != "") {
                setParams(MainActivity.viewModel.nowPos.value!!, width = Integer.parseInt(widthStr))
            }
        }
        wText = view.findViewById<TextView>(R.id.width)
        hText = view.findViewById<TextView>(R.id.height)
        val displaySize = view.findViewById<TextView>(R.id.display_size)
        displaySize.text =
            getString(R.string.disp_size) + "${MainActivity.dWidth} × ${MainActivity.dHeight}"
    }

    private fun setNowPosValue() {
        val viewModel = MainActivity.viewModel
        val height = viewModel.nowHeight.toString()
        val width = viewModel.nowWidth.toString()
        title.text = viewModel.nowPos.value
        hEdit.setText(height)
        wEdit.setText(width)
        hText.text = height
        wText.text = width
        switch.isChecked = viewModel.nowVisible
    }

    //positionが変わったときに呼ばれる
    private fun onChangePosition() {
        allowChangeVisible = false
        setNowPosValue()
        allowChangeVisible = true
    }

    //高さ・幅の変更
    private fun setParams(position: String, height: Int = -1, width: Int = -1) {

        when (position) {
            KeyStore.TOP -> MainActivity.viewModel.setTopParam(height, width)
            KeyStore.BOTTOM -> MainActivity.viewModel.setBottomParam(height, width)
            KeyStore.RIGHT -> MainActivity.viewModel.setRightParam(height, width)
            else -> MainActivity.viewModel.setLeftParam(height, width)
        }
        //プリファレンス（設定）に保存
        context!!.getSharedPreferences(PREF.Name.key, AppCompatActivity.MODE_PRIVATE).edit().apply {
            if (height > 0) putInt(position + "Height", height)
            if (width > 0) putInt(position + "Width", width)
            apply()
        }
    }

    //Visibleの変更
    private fun setVisible(position: String, visible: Boolean) {

        when (position) {
            KeyStore.TOP -> MainActivity.viewModel.setTopVisible(visible)
            KeyStore.BOTTOM -> MainActivity.viewModel.setBottomVisible(visible)
            KeyStore.RIGHT -> MainActivity.viewModel.setRightVisible(visible)
            else -> MainActivity.viewModel.setLeftVisible(visible)
        }
        //プリファレンス（設定）に保存
        context!!.getSharedPreferences(PREF.Name.key, AppCompatActivity.MODE_PRIVATE).edit().apply {
            putBoolean(position + "Visible", visible)
            apply()
        }
    }

}