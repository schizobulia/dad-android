package com.example.dad.bean

import java.io.Serializable

class Movice : Serializable {
    var url: String = ""
        get() = field
        set(value) {
            field = value
        }

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
}