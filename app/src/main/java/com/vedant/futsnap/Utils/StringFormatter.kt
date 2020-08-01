package com.vedant.futsnap.Utils

import java.text.NumberFormat
import java.util.*

class StringFormatter{
    companion object{
        fun getLocaleFormattedStringFromNumber(value:Int?):String {
            return if (value != null){
                NumberFormat.getNumberInstance(Locale.getDefault()).format(value)
            }
            else{
                ""
            }
        }
        fun getNumberFromLocaleFormattedString(value:String) = NumberFormat.getNumberInstance(Locale.getDefault()).parse(value)!!.toInt()
    }
}