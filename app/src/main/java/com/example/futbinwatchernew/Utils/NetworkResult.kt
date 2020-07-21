package com.example.futbinwatchernew.Utils

import com.example.futbinwatchernew.UI.ErrorHandling.Error

sealed class NetworkResponse<out T>{
    class Success<T>(val data: T):NetworkResponse<T>()
    class Failure(val error: Error):NetworkResponse<Nothing>()
}
