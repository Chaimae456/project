package com.example.carpartsalpha.outils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class Button(context: Context,attrs : AttributeSet) : AppCompatButton(context,attrs) {
    init {
        applyFont()
    }
    private fun applyFont(){
        val buttonTypeface : Typeface = Typeface.createFromAsset(context.assets,"alibaba_sans_heavy.ttf")
        typeface = buttonTypeface
    }
}