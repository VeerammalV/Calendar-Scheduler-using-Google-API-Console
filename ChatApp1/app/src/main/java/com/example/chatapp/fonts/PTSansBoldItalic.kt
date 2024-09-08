package com.example.chatapp.fonts

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class PTSansBoldItalic : AppCompatTextView {
    constructor(context: Context) : super(context) {

        initial()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {

        initial()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

        initial()
    }

    private fun initial() {

        val typeface = Typeface.createFromAsset(context.assets, "PTSans_BoldItalic.ttf")
        this.typeface = typeface
    }
}