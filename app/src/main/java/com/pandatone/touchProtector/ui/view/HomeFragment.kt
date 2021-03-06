package com.pandatone.touchProtector.ui.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.pandatone.touchProtector.MainActivity
import com.pandatone.touchProtector.PREF
import com.pandatone.touchProtector.R
import com.pandatone.touchProtector.databinding.FragmentHomeBinding
import com.pandatone.touchProtector.ui.dialog.ColorDialogFragment
import com.pandatone.touchProtector.ui.dialog.IconDialogFragment
import com.pandatone.touchProtector.ui.overlay.OverlayService
import com.pandatone.touchProtector.ui.viewModel.HomeViewModel
import soup.neumorphism.NeumorphButton
import soup.neumorphism.NeumorphImageButton


/**
 * A placeholder fragment containing a simple view.
 */
class HomeFragment : Fragment() {

    private lateinit var toggle: NeumorphButton
    private lateinit var seekBar: SeekBar
    private lateinit var transCheck: CheckBox
    private lateinit var iconChoiceButton: ImageButton
    private lateinit var colorChoiceButton: ImageButton

    companion object {
        lateinit var viewModel: HomeViewModel
        var iconDialog: IconDialogFragment? = null
        var colorDialog: ColorDialogFragment? = null
    }

    object CustomBindingAdapter {

        @BindingAdapter("src")
        @JvmStatic
        fun ImageButton.setImageId(id: Int) {
            setImageResource(id)
        }

        @BindingAdapter("color")
        @JvmStatic
        fun NeumorphImageButton.setColorId(id: Int) {
            setBackgroundColor(ColorStateList.valueOf(resources.getColor(id)))
        }

        @BindingAdapter("iconColor")
        @JvmStatic
        fun NeumorphImageButton.setIconColorId(id: Int) {
            imageTintList = ColorStateList.valueOf(resources.getColor(id))
        }

        @BindingAdapter("toggleText")
        @JvmStatic
        fun NeumorphButton.setToggleText(active: Boolean) {
            text = if (active) "ON" else "OFF"
        }

        @BindingAdapter("toggleResource")
        @JvmStatic
        fun NeumorphButton.setToggleResource(active: Boolean) {
            if (active) {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.toggle_bar_on)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.toggle_bar_off)
            }
        }
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
            setOnClickListener {
                val isChecked = OverlayService.isActive
                SettingFragment.allowChangeVisible = isChecked
                OverlayService.transBackground = transCheck.isChecked
                if (!isChecked) {
                    OverlayService.start(context)
                } else {
                    OverlayService.stop(context)
                }
            }
        }

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
        toggle = view.findViewById(R.id.toggle_button)
        transCheck = view.findViewById(R.id.transparent_check)
        seekBar = view.findViewById(R.id.seekBar)
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
        colorChoiceButton.setOnClickListener {
            colorDialog = ColorDialogFragment()
            fragmentManager?.run {
                colorDialog?.show(this, "ColorListDialog")
            }
        }

        val statusView = view.findViewById<TextView>(R.id.status)
        var count = 0
        do {
            Thread.sleep(100)
            statusView.text = MainActivity.statusText
            count++
        } while (MainActivity.statusText == "No Data" && count < 30)

        viewModel.setToggleStatus(OverlayService.isActive)

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