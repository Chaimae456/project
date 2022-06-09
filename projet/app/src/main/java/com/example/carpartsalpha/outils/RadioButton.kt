package com.example.carpartsalpha.outils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class RadioButton(context: Context,attrs : AttributeSet) : AppCompatRadioButton(context,attrs) {
    init {
        applyFont()
    }

    private fun applyFont(){
        val editTypeface : Typeface = Typeface.createFromAsset(context.assets , "alibaba_sans_heavy.ttf")
        typeface = editTypeface
    }


}