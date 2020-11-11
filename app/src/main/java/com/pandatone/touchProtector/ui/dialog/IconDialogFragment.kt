package com.pandatone.touchProtector.ui.dialog

import android.app.Dialog
import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import com.pandatone.touchProtector.R

class IconDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!)
        val dw = dialog.window
        dw?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.icon_list)
        val none = dialog.findViewById<TextView>(R.id.icon_button_none)
        none.setOnClickListener {
            //クリック時実行したい処理を記述
            //動作デモではここでChromeCustomTabを開いています。
            dialog.dismiss()
        }
        return dialog
    }

}