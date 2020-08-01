package com.vedant.futsnap.UI

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
import com.vedant.futsnap.FUTBINWatcherApp
import com.vedant.futsnap.Network.ResponseModels.Client
import com.vedant.futsnap.UI.ViewModels.MainActivityViewModel
import com.vedant.futsnap.R
import com.vedant.futsnap.UI.ErrorHandling.Error
import com.vedant.futsnap.UI.ErrorHandling.ErrorType
import com.vedant.futsnap.UI.ViewModels.CustomViewModelFactory
import com.vedant.futsnap.Utils.*
import javax.inject.Inject


class ErrorFragment(private val error: Error, private val retryAction:
((client: Client) -> LiveData<NetworkResponse<Int>>)?):Fragment() {

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
            val clientToken = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.FIREBASE_TOKEN_KEY)
                    as String
            val clientId = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.CLIENT_ID) as Int
            val clientEmail = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.CLIENT_EMAIL) as String
            val client = Client(clientId,clientEmail, clientToken, null)
            btn_retryButton.visibility = View.VISIBLE
            btn_retryButton.setOnClickListener{
                this.invoke(client).observe(requireActivity(), Observer {response ->
                    when (response) {
                        is NetworkResponse.Success -> {
                            sharedPrefRepo.writeToSharedPref(SharedPrefsTags.CLIENT_ID, response.data)
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