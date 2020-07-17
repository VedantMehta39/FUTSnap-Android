package com.example.futbinwatchernew.Utils

sealed class NetworkResponse<out T>{
    class Success<T>(val data: T):NetworkResponse<T>()
    class Failure(val error:Error):NetworkResponse<Nothing>()
}
