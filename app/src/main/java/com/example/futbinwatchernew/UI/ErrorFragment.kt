package com.example.futbinwatchernew.UI

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.futbinwatchernew.FUTBINWatcherApp
import com.example.futbinwatchernew.UI.ViewModels.MainActivityViewModel
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.UI.ErrorHandling.Error
import com.example.futbinwatchernew.UI.ErrorHandling.ErrorType
import com.example.futbinwatchernew.Utils.*
import javax.inject.Inject


class ErrorFragment(private val error: Error, private val retryAction:
((context: Context, newToken:String) -> LiveData<NetworkResponse<Int>>)?):Fragment() {

    @Inject
    lateinit var customViewModelFactory: CustomViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext()).inflate(R.layout.error_view,container,false)
    }

    override fun onAttach(context: Context) {
        FUTBINWatcherApp.component.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val iv_error_icon = view.findViewById<ImageView>(R.id.ic_error)
        when(error.type){
            ErrorType.GENERAL -> iv_error_icon.setImageResource(R.drawable.ic_error)
            ErrorType.SERVER -> iv_error_icon.setImageResource(R.drawable.ic_server_error)
            ErrorType.REGISTRATION -> iv_error_icon.setImageResource(R.drawable.ic_registration_error)
        }
        val tv_error_message = view.findViewById<TextView>(R.id.error_message)
        tv_error_message.text = error.message
        val btn_retryButton = view.findViewById<Button>(R.id.retry_button)
        retryAction?.run {
            val vm = ViewModelProvider(requireActivity(),customViewModelFactory).get(
                MainActivityViewModel::class.java)
            val sharedPrefRepo = SharedPrefRepo(requireActivity(), SharedPrefFileNames.CLIENT_REGISTRATION)
            val token = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.FIREBASE_TOKEN_KEY)
                    as String
            btn_retryButton.visibility = View.VISIBLE
            btn_retryButton.setOnClickListener{
                this.invoke(requireContext(), token).observe(requireActivity(), Observer {response ->
                    when (response) {
                        is NetworkResponse.Success -> {
                            sharedPrefRepo.writeToSharedPref(SharedPrefsTags.CLIENT_ID, response.data)
                            sharedPrefRepo.writeToSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC, true)
                            vm.clientId = response.data
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_view_tag,
                                    SearchedPlayersFragment())
                                .commit()
                        }
                    }
                })
            }
        }

    }
}