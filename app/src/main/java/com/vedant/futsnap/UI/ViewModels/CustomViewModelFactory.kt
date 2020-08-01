package com.vedant.futsnap.UI.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider


@Suppress("UNCHECKED_CAST")
class CustomViewModelFactory @Inject constructor(private var viewModelMap:Map<Class<out ViewModel>,@JvmSuppressWildcards Provider<ViewModel>>):ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModelMap[modelClass]?.get() as T
    }

}