package com.dalmazo.helena.mestrerpg.util

import android.widget.EditText

class Utils {

    companion object {
        fun enableEditText(editText: EditText) {
            editText.isEnabled = true
        }

        fun disableEditText(editText: EditText) {
            editText.isEnabled = false
        }
    }

}