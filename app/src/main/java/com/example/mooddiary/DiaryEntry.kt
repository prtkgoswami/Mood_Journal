package com.example.mooddiary

import java.text.SimpleDateFormat
import java.util.*

class DiaryEntry {
    var postDate: String? = null
    var postEmotion: String? = null
    var postData: String? = null
    var postTime: String? = null

    constructor() : super() {}

    constructor(PostEmotion: String, PostData: String) : super() {
        val sdf_date = SimpleDateFormat("dd/M/yyyy")
        val currDate = sdf_date.format(Date())
        val sdf_time = SimpleDateFormat("HH:mm:ss")
        val currTime = sdf_time.format(Date())

        this.postDate = currDate
        this.postTime = currTime
        this.postEmotion = PostEmotion
        this.postData = PostData
    }
}

data class Page (
    var entry : DiaryEntry? = null){
}