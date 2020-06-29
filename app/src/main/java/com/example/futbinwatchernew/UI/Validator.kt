package com.example.futbinwatchernew.UI

interface Validator {
    var errorMessage:String
    fun validate(data:String):Boolean
}