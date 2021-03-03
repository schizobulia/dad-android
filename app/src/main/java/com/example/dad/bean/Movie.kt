package com.example.dad.bean

import java.io.Serializable

class Movie : Serializable {
    var img: String = ""
        get() = field
        set(value) {
            field = value
        }

    var title: String = ""
        get() = field
        set(value) {
            field = value
        }

    /**
     * 电视剧播放列表
     */
    var list: ArrayList<String> = ArrayList()
        get() = field
        set(value) {
            field = value
        }
}