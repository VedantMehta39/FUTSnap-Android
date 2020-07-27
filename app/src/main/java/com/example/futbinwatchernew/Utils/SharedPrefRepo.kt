package com.example.futbinwatchernew.Utils

import android.content.Context
import android.content.SharedPreferences

enum class SharedPrefsTags{
    FIREBASE_TOKEN_KEY,
    CLIENT_ID,
    CLIENT_EMAIL
}

enum class SharedPrefFileNames{
    CLIENT_REGISTRATION
}

class SharedPrefRepo(context: Context, fileName:SharedPrefFileNames){
    var sharedPref:SharedPreferences = context.getSharedPreferences(fileName.toString(), Context.MODE_PRIVATE)

    fun readFromSharedPref(TAG: SharedPrefsTags):Any?{
        when(TAG){
            SharedPrefsTags.FIREBASE_TOKEN_KEY ->{
                return sharedPref.getString(TAG.toString(), null)
            }
            SharedPrefsTags.CLIENT_ID ->{
                return sharedPref.getInt(TAG.toString(), 0)
            }
            SharedPrefsTags.CLIENT_EMAIL ->{
                return sharedPref.getString(TAG.toString(), null)
            }
        }
    }
    fun writeToSharedPref(TAG:SharedPrefsTags, value:Any){
        with(sharedPref.edit()){
            when(TAG){
                SharedPrefsTags.FIREBASE_TOKEN_KEY ->{
                    putString(TAG.toString(), value as String)
                }
                SharedPrefsTags.CLIENT_ID ->{
                    putInt(TAG.toString(), value as Int)
                }
                SharedPrefsTags.CLIENT_EMAIL -> {
                    putString(TAG.toString(), value as String)
                }
            }
            commit()
        }
    }

}