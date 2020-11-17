package com.pandatone.touchProtector.ui.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pandatone.touchProtector.MainActivity
import com.pandatone.touchProtector.PREF
import com.pandatone.touchProtector.R
import com.pandatone.touchProtector.databinding.FragmentHomeBinding
import com.pandatone.touchProtector.ui.dialog.IconDialogFragment
import com.pandatone.touchProtector.ui.overlay.OverlayService
import com.pandatone.touchProtector.ui.viewModel.HomeViewModel


/**
 * A placeholder fragment containing a simple view.
 */
class HomeFragment : Fragment() {

    private lateinit var toggle: ToggleButton
    private lateinit var seekBar: SeekBar
    private lateinit var transCheck: CheckBox
    private lateinit var iconChoiceButton: ImageButton
    private lateinit var colorChoiceButton: ImageButton

    companion object {
        lateinit var viewModel: HomeViewModel
        var iconDialog: IconDialogFragment? = null
    }

    object CustomBindingAdapter {

        @BindingAdapter("src")
        @JvmStatic
        fun ImageButton.setImageId(id:Int) {
            setImageResource(id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setViews(view)

        // ON/OFFのトグルボタン切り替え
        toggle.apply {
            isChecked = OverlayService.isActive
            setOnCheckedChangeListener { _, isChecked ->
                SettingFragment.allowChangeVisible = isChecked
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

        iconChoiceButton = view.findViewById(R.id.icon_choice_button)
        iconChoiceButton.setOnClickListener {
            iconDialog = IconDialogFragment()
            fragmentManager?.run {
                iconDialog?.show(this, "IconListDialog")
            }
        }

        colorChoiceButton = view.findViewById(R.id.color_choice_button)

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
        viewModel.setIconSize(progress)
        context!!.getSharedPreferences(PREF.Name.key, AppCompatActivity.MODE_PRIVATE).edit().apply {
            putInt(PREF.IconSize.key, progress)
            apply()
        }
    }

}