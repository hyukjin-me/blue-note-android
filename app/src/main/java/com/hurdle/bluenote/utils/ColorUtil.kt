package com.hurdle.bluenote.utils

import android.graphics.ColorFilter
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat

// https://developer.android.com/reference/android/graphics/PorterDuff.Mode
// SRC_ATOP, 대상 픽셀을 포함하지 않는 소스 픽셀을 버립니다.
fun colorBlendFilter(color: Int): ColorFilter? {
    return BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
        color, BlendModeCompat.SRC_ATOP
    )
}