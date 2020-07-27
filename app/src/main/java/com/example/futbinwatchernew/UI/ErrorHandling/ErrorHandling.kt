package com.example.futbinwatchernew.UI.ErrorHandling

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import com.example.futbinwatchernew.Network.ResponseModels.Client
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.UI.ErrorFragment
import com.example.futbinwatchernew.Utils.NetworkResponse
import com.example.futbinwatchernew.Utils.SharedPrefFileNames
import com.example.futbinwatchernew.Utils.SharedPrefRepo
import com.example.futbinwatchernew.Utils.SharedPrefsTags
import com.pranavpandey.android.dynamic.toasts.DynamicToast

class ErrorHandling(var context:Context, var fragmentManager: FragmentManager,
                    var retryAction:((client: Client) -> LiveData<NetworkResponse<Int>>)?) {

    fun handle(error: Error){
        when(error){
            is Error.GeneralError -> {
                DynamicToast.makeError(context, error.message, Toast.LENGTH_LONG).show()
            }
            is Error.RegistrationError ->{
                fragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container_view_tag,
                        ErrorFragment(error,retryAction), "ERROR_FRAG"
                    ).commit()
            }
            is Error.ServerError ->{
                fragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container_view_tag,
                        ErrorFragment(error,null), "ERROR_FRAG"
                    ).commit()
            }

        }
    }
}