package com.example.futbinwatchernew.Utils

import java.text.NumberFormat
import java.util.*

class StringFormatter{
    companion object{
        fun getLocaleFormattedStringFromNumber(value:Int) = NumberFormat.getNumberInstance(Locale.getDefault()).format(value)
        fun getNumberFromLocaleFormattedString(value:String) = NumberFormat.getNumberInstance(Locale.getDefault()).parse(value)!!.toInt()
    }
}