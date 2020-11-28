package com.pandatone.touchProtector.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.pandatone.touchProtector.R
import com.pandatone.touchProtector.databinding.ColorListBinding
import com.pandatone.touchProtector.ui.view.HomeFragment

class ColorDialogFragment : DialogFragment() {

    //すべてBindingで処理
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!)
        val binding = DataBindingUtil.inflate<ColorListBinding>(
            LayoutInflater.from(activity),
            R.layout.color_list,
            null,
            false
        )
        binding.viewModel = HomeFragment.viewModel
        val dw = dialog.window
        dw?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        return dialog
    }

}