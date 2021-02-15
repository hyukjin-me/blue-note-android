package com.hurdle.bluenote.utils

const val SHEET = "sheet"
const val NOTE = "note"

const val ONE_SECOND = 1000L
const val ONE_MINUTE = 60 * ONE_SECOND
const val ONE_HOUR = 60 * ONE_MINUTE

// 검색시 최대로 입력할 수 있는 문자열의 갯수
const val MAX_SEARCH_LENGTH = 12

// 시트 생성시 최대로 생성할 수 있는 Question 갯수
const val MAX_QUESTION = 200

object HelperConstants {
    // 사용자가 '다른앱 위에 그리기' 권한에 동의 이후 다시 앱으로 돌아올때 확인을 위한 상수
    const val REQUEST_CODE_OVERLAY_PERMISSION = 10002

    // Intent 전달시 key값
    const val SHEET_ID = "sheet_id"

    const val INIT_NONE = "NONE"
    const val INIT_PREPARE = "INIT_PREPARE"
    var SHEET_STATE = INIT_NONE

    // 서비스 종료시, 뷰를 지워야하는데 어떤 뷰를 지워야하는지 알려주는 상수
    // INIT_NONE, ICON_VIEW, EXPANSION_VIEW
    var HELPER_VIEW_STATE = INIT_NONE
    const val ICON_VIEW = "helper.icon"
    const val EXPANSION_VIEW = "helper.exapnasion"

    // 시작/종료 버튼의 태그 설정을 위한 상수
    const val PLAY_TAG = "play"
    const val STOP_TAG = "stop"

    // 알림 관련 상수
    const val NOTIFICATION_ID = 10_003
    const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"

    var HELPER_FOREGROUND_STATE = INIT_NONE
    const val HELPER_STOP = "helper.action.stop"
}