package com.example.futbinwatchernew

import java.text.NumberFormat
import java.util.*

class Util{
    companion object{
        fun getLocaleFormattedStringFromNumber(value:Int) = NumberFormat.getNumberInstance(Locale.getDefault()).format(value)
        fun getNumberFromLocaleFormattedString(value:String) = NumberFormat.getNumberInstance(Locale.getDefault()).parse(value)!!.toInt()
    }
}