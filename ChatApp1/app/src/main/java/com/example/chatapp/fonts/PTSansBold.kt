package com.example.chatapp.fonts
import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
class PTSansBold : AppCompatTextView {

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

            val typeface = Typeface.createFromAsset(context.assets, "PTSans_Bold.ttf")
            this.typeface = typeface
        }

}


