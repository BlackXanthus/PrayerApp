package com.churchinwales.prayer

import android.util.Log

class AppDebug {
    companion object {
        @JvmStatic
        val on: Boolean = false

        @JvmStatic
        fun log(tag: String, message: String) {
            if (this.on) {
                Log.v(tag, message)
            }
        }
    }
}