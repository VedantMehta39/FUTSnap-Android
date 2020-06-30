package com.example.futbinwatchernew.UI

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.Models.Platform
import com.example.futbinwatchernew.Models.PlayerDialogFragModel
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.SearchPlayerViewModel
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import java.util.*

class SinglePlayerDialog:DialogFragment() {

    companion object{
        const val PLAYER_RATING = "PLAYER_RATING"
        const val PLAYER_ID = "PLAYER_ID"
        const val PLAYER_NAME = "PLAYER_NAME"
        const val PS_PLAYER_CURRENT_PRICE = "PS_PLAYER_CURRENT_PRICE"
        const val XBOX_PLAYER_CURRENT_PRICE = "XBOX_PLAYER_CURRENT_PRICE"
        const val PLAYER_IMAGE_FILE_PATH = "PLAYER_IMAGE_FILE_PATH"


        fun newInstance(data: PlayerDialogFragModel) = SinglePlayerDialog().apply {
            arguments = Bundle().apply {
                putInt(PLAYER_ID, data.id)
                putInt(PLAYER_RATING, data.rating)
                putString(PLAYER_NAME, data.name)
                putInt(PS_PLAYER_CURRENT_PRICE, data.currentPrice.get(Platform.PS)!!)
                putInt(XBOX_PLAYER_CURRENT_PRICE,data.currentPrice.get(Platform.XB)!!)
                putString(PLAYER_IMAGE_FILE_PATH, data.imageURL)
            }
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.single_player_dialog, null)
        val currentPrice = EnumMap<Platform,Int>(Platform::class.java)
        currentPrice.put(Platform.PS,requireArguments().getInt(PS_PLAYER_CURRENT_PRICE))
        currentPrice.put(Platform.XB,requireArguments().getInt(XBOX_PLAYER_CURRENT_PRICE))
        val data = PlayerDialogFragModel(
                requireArguments().getInt(PLAYER_ID),
                requireArguments().getString(PLAYER_NAME)!!,
                requireArguments().getString(PLAYER_IMAGE_FILE_PATH)!!,
                requireArguments().getInt(PLAYER_RATING),
                currentPrice
        )
        val currentPriceTextView = view.findViewById<TextView>(R.id.tv_current_price)
        currentPriceTextView.text = data.currentPrice.get(Platform.PS).toString()
        Picasso.get().load(data.imageURL).into(view.findViewById<ImageView>(R.id.img_player))
        view.findViewById<TextView>(R.id.tv_player_name).text = (data.name + " " + data.rating.toString())
        view.findViewById<Button>(R.id.ps_button).setOnClickListener {
            currentPriceTextView.text = data.currentPrice.get(Platform.PS).toString()
        }
        view.findViewById<Button>(R.id.xbox_button).setOnClickListener {
            currentPriceTextView.text = data.currentPrice.get(Platform.XB).toString()
        }
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

        val platform_toggle = view.findViewById<MaterialButtonToggleGroup>(R.id.platform_toggle)
        val gte_lt_toggle = view.findViewById<MaterialButtonToggleGroup>(R.id.gte_lt_toggle)

        platform_toggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (group.checkedButtonId == -1) group.check(checkedId)
        }




        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (targetPrice.isValid and gte_lt_toggle.checkedButtonIds.isNotEmpty()) {
                val notifiedPlayer = PlayerDBModel()
                notifiedPlayer.id = data.id
                notifiedPlayer.name = data.name
                notifiedPlayer.currentPrice = currentPriceTextView.text.toString().toInt()
                notifiedPlayer.imageURL = data.imageURL
                notifiedPlayer.rating = data.rating
                val chosenPlatformId = platform_toggle.checkedButtonId
                val chosenComparisonDirectionIds = gte_lt_toggle.checkedButtonIds
                when(chosenPlatformId){
                    R.id.ps_button -> notifiedPlayer.platform = Platform.PS
                    R.id.xbox_button -> notifiedPlayer.platform = Platform.XB
                }
                if(chosenComparisonDirectionIds.contains(R.id.gte_target)){
                    notifiedPlayer.gte = true
                }
                if(chosenComparisonDirectionIds.contains(R.id.lt_target)){
                    notifiedPlayer.lt = true
                }
                notifiedPlayer.targetPrice = targetPrice.text.toString().toInt()
                val vm = ViewModelProvider(requireActivity()).get(SearchPlayerViewModel::class.java)
                vm.allTrackedPlayers.add(notifiedPlayer)
                this.dismiss()
                dialog.cancel()
            }
        }
        return dialog
    }



}