package com.example.futbinwatchernew.UI.Validators

interface Validator<T> {
    fun validate(data:T):Boolean
}