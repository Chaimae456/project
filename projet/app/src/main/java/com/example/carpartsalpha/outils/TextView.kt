package com.example.carpartsalpha.outils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TextView(context: Context, attrs : AttributeSet) : AppCompatTextView(context , attrs) {
    init {
        applyFont()
    }
    private fun applyFont(){
        val normalTypeface :Typeface = Typeface.createFromAsset(context.assets,"alibaba_sans_heavy.ttf")
        typeface = normalTypeface
    }
}