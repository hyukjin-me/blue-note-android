package com.hurdle.bluenote.utils

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.hurdle.bluenote.R

// https://developer.android.com/reference/android/graphics/PorterDuff.Mode
// SRC_ATOP, 기존색 덮어사용
fun colorBlendFilter(color: Int): ColorFilter? {
    return BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
        color, BlendModeCompat.SRC_ATOP
    )
}

fun setButtonTextColor(context: Context, button: Button, isSelect: Boolean) {
    if (isSelect) {
        // 버튼 선택
        button.setBackgroundColor(Color.RED)
    } else {
        // 기본 상태
        val color = ContextCompat.getColor(context, R.color.blue_btn)
        button.setBackgroundColor(color)
    }
}