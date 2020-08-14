package com.dalmazo.helena.mestrerpg.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class Utils {

    companion object {
        fun getByteArrayFromImageView(imageView: ImageView):ByteArray {
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }
    }
}