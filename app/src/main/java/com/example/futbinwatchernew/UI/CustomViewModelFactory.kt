package com.example.futbinwatchernew.UI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class CustomViewModelFactory(val clientId:Int):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Int::class.java).newInstance(clientId)
    }


}