package com.pandatone.touchProtector.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pandatone.touchProtector.MainActivity
import com.pandatone.touchProtector.OverlayService
import com.pandatone.touchProtector.PREF
import com.pandatone.touchProtector.R
import kotlin.math.min

/**
 * A placeholder fragment containing a simple view.
 */
class HomeFragment : Fragment() {

    private lateinit var toggle: ToggleButton
    private lateinit var seekBar: SeekBar
    private lateinit var transCheck: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setViews(view)

        // ON/OFFのトグルボタン切り替え
        toggle.apply {
            isChecked = OverlayService.isActive
            setOnCheckedChangeListener { _, isChecked ->
                SettingsFragment.allowOnClick = isChecked
                OverlayService.transBackground = transCheck.isChecked
                if (isChecked) OverlayService.start(context)
                else OverlayService.stop(context)
            }
        }

        val pref = context!!.getSharedPreferences(PREF.Name.key, AppCompatActivity.MODE_PRIVATE)

        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                //ツマミがドラッグされると呼ばれる
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    changeIconSize(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // ツマミがタッチされた時に呼ばれる
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // ツマミがリリースされた時に呼ばれる
                }
            }
        )
    }

    private fun setViews(view: View) {
        toggle = view.findViewById<ToggleButton>(R.id.toggle_button)
        transCheck = view.findViewById<CheckBox>(R.id.transparent_check)
        seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        seekBar.progress =
            context!!.getSharedPreferences(PREF.Name.key, AppCompatActivity.MODE_PRIVATE)
                .getInt(PREF.IconSize.key, 100)

        val statusView = view.findViewById<TextView>(R.id.status)
        var count = 0
        do {
            Thread.sleep(100)
            statusView.text = MainActivity.statusText
            count++
        } while (MainActivity.statusText == "No Data" && count < 30)
    }

    //アイコンサイズの変更
    private fun changeIconSize(progress: Int) {
        MainActivity.viewModel.setIconSize(progress)
        context!!.getSharedPreferences(PREF.Name.key, AppCompatActivity.MODE_PRIVATE).edit().apply {
            putInt(PREF.IconSize.key, progress)
            apply()
        }
    }

}