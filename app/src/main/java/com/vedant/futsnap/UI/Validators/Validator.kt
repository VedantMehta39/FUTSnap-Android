package com.vedant.futsnap.UI.Validators

interface Validator<T> {
    fun validate(data:T):Boolean
}