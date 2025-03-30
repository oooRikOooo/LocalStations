package com.andriipedosenko.localstations.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.andriipedosenko.localstations.R
import com.andriipedosenko.localstations.databinding.AppTextFieldBinding
import com.google.android.material.textfield.TextInputLayout

class AppTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    var value: String
        get() = binding.editText.text.toString()
        set(value) {
            if (binding.editText.text.toString() != value) {
                binding.editText.setText(value)
            }
        }

    private val binding: AppTextFieldBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = AppTextFieldBinding.inflate(inflater, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AppTextField,
            0, 0
        ).apply {
           try {
               val hintText = getString(R.styleable.AppTextField_android_hint)
               val imeOptions = getInt(R.styleable.AppTextField_android_imeOptions, 0)
               val inputType = getInt(R.styleable.AppTextField_android_inputType, 0)

               with(binding) {
                   textInputLayout.hint = hintText
                   editText.imeOptions = imeOptions
                   editText.inputType = inputType
                   editText.isSingleLine = true
               }
           } finally {
               recycle()
           }
        }
    }

}