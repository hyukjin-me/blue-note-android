package com.hurdle.bluenote.data

data class Home(
    val idText: String = "",
    val notes: List<Note>? = null,
    val sheets: List<Sheet>? = null
)