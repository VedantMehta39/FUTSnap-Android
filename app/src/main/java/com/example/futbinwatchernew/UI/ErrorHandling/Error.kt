package com.example.futbinwatchernew.UI.ErrorHandling

enum class ErrorType{
    GENERAL,
    SERVER,
    REGISTRATION
}

sealed class Error(val type: ErrorType, var message:String){
    class RegistrationError: Error(
        ErrorType.REGISTRATION, "User registration has failed. " +
            "You may not receive notifications. Please try again later!")

    class ServerError(message: String = "Couldn't connect to server. Please try again later!"):
        Error(ErrorType.SERVER, message)

    class GeneralError(message: String):
        Error(ErrorType.GENERAL, message)


}

