package com.example.futbinwatchernew.UI

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.SearchPlayerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso

class SinglePlayerDialog:DialogFragment() {

    companion object{
        const val PLAYER_RATING = "PLAYER_RATING"
        const val PLAYER_ID = "PLAYER_ID"
        const val PLAYER_NAME = "PLAYER_NAME"
        const val PLAYER_CURRENT_PRICE = "PLAYER_CURRENT_PRICE"
        const val PLAYER_IMAGE_FILE_PATH = "PLAYER_IMAGE_FILE_PATH"


        fun newInstance(data: PlayerDBModel) = SinglePlayerDialog().apply {
            arguments = Bundle().apply {
                putInt(PLAYER_ID, data.id)
                putInt(PLAYER_RATING, data.rating)
                putString(PLAYER_NAME, data.name)
                putInt(PLAYER_CURRENT_PRICE, data.currentPrice)
                putString(PLAYER_IMAGE_FILE_PATH, data.imageURL)
            }
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.single_player_dialog, null)

        val data = PlayerDBModel(
            SearchPlayerResponse(
                requireArguments().getInt(PLAYER_ID),
                requireArguments().getString(PLAYER_NAME)!!,
                requireArguments().getInt(PLAYER_RATING),
                requireArguments().getString(PLAYER_IMAGE_FILE_PATH)!!
            ),
            requireArguments().getInt(PLAYER_CURRENT_PRICE)
        )
        view.findViewById<TextView>(R.id.tv_current_price).text = data.currentPrice.toString()
        Picasso.get().load(data.imageURL).into(view.findViewById<ImageView>(R.id.img_player))
        view.findViewById<TextView>(R.id.tv_player_name).text = (data.name + " " + data.rating.toString())
        val targetPrice = (view.findViewById<ValidatedEditText>(R.id.et_target_price))
        targetPrice.setValidator( object :
            Validator {
            override var errorMessage: String = ""
                get() = field

            override fun validate(data: String): Boolean {
                if(data.isEmpty()){
                    errorMessage = "Target Price cannot be empty"
                    return false
                }
                if(data.contains("-")){
                    errorMessage = "Target Price cannot be negative"
                    return false
                }
                if(data.contains(".")){
                    errorMessage = "Target Price has to be a whole number"
                    return false
                }
                return true
            }

        })
        builder.setView(view).setNegativeButton(R.string.cancel) { _, _ ->
            dialog!!.cancel()
        }.setPositiveButton(R.string.ok,null)

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (targetPrice.isValid) {
                data.targetPrice = targetPrice.text.toString().toInt()
                val vm = ViewModelProvider(requireActivity()).get(SearchPlayerViewModel::class.java)
                vm.allTrackedPlayers.add(data)
                this.dismiss()
                dialog!!.cancel()
            }
        }
        return dialog
    }


}