package com.hurdle.bluenote.utils

import android.os.SystemClock
import android.widget.Chronometer

object QuestionChronometer {
    var lastElapsedRealTime = 0L

    fun start(chronometer: Chronometer) {
        if (lastElapsedRealTime == 0L) {
            // 처음 시작
            chronometer.start()
        } else {
            // 재시작
            val intervalTime = SystemClock.elapsedRealtime() - lastElapsedRealTime
            chronometer.base += intervalTime
            chronometer.start()
        }

        // 크로노미터 1초부터 시작
        startFirstChronometer(chronometer)
    }

    fun stopChronometer(chronometer: Chronometer) {
        chronometer.stop()
        lastElapsedRealTime = SystemClock.elapsedRealtime()
    }

    fun resetElapsedRealTime() {
        lastElapsedRealTime = 0L
    }

    // 화면 진입후 10초뒤 시작 버튼을 클릭시 10초부터 시작되는 현상 방지
    // https://stackoverflow.com/questions/19194302/android-chronometer-resume-function
    // Chronometer.setBase(SystemClock.elapsedRealtime() - (min * 60000 + second * 1000))
    fun reBaseChronometer(chronometer: Chronometer, timeText: String): Chronometer {
        val splitTimeText: List<String> = timeText.split(':')
        val minute: Int = splitTimeText[0].toInt()
        val second: Int = splitTimeText[1].toInt()

        if (minute == 0 && second == 0) {
            chronometer.base = SystemClock.elapsedRealtime()
        } else {
            val baseTime = minute * 60000 + second * 1000
            chronometer.base = SystemClock.elapsedRealtime() - baseTime
        }

        return chronometer
    }

    // 크로노미터 Start, 0초부터 시작
    fun startFirstChronometer(chronometer: Chronometer) {
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    // 크로노미터 Start, 1초부터 시작, 연속적으로 재시작이 필요할때 사용
    fun resetChronometer(chronometer: Chronometer) {
        chronometer.base = SystemClock.elapsedRealtime() - 1000
        chronometer.start()
    }

    // 크로노미터 Start, (Pause -> Start)
    fun continueChronometer(chronometer: Chronometer) {
        val intervalTime = SystemClock.elapsedRealtime() - lastElapsedRealTime
        chronometer.base += intervalTime
        chronometer.start()
    }
}