package com.vedant.futsnap.Utils

import com.vedant.futsnap.UI.ErrorHandling.Error

sealed class NetworkResponse<out T>{
    class Success<T>(val data: T):NetworkResponse<T>()
    class Failure(val error: Error):NetworkResponse<Nothing>()
}
