package com.example.futbinwatchernew.UI

import android.app.Dialog
import android.os.Bundle
import android.text.util.Linkify
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.futbinwatchernew.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class NotificationsProblemInfoFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Alert").setMessage(R.string.notification_problem_info)
            .setNeutralButton(R.string.ok){_,_ ->
            dismiss()
        }
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        Linkify.addLinks((dialog!!.findViewById(android.R.id.message) as TextView), Linkify.ALL)

    }

}