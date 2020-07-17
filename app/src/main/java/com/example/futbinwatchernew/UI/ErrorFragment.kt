package com.example.futbinwatchernew.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.Utils.Error
import com.example.futbinwatchernew.Utils.ErrorType


class ErrorFragment(private val error: Error, private val retryAction: (() -> Unit)?):Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext()).inflate(R.layout.error_view,container,false)
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
            btn_retryButton.visibility = View.VISIBLE
            btn_retryButton.setOnClickListener{
                this.invoke()
            }
        }

    }
}