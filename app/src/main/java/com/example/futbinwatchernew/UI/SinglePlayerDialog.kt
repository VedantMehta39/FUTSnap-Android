package com.example.futbinwatchernew.UI

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.Models.Platform
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.SearchPlayerViewModel
import com.example.futbinwatchernew.Util
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso

class SinglePlayerDialog:DialogFragment() {

    companion object{

        var instance:SinglePlayerDialog? = null

        fun newInstance():SinglePlayerDialog {
            if (instance == null){
                instance = SinglePlayerDialog()
            }
            return instance!!
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.single_player_dialog, null)
        builder.setView(view).setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }.setPositiveButton(R.string.ok,null)
        val dialog = builder.create()
        dialog.show()


        val playerImageView = view.findViewById<ImageView>(R.id.img_player)
        val currentPriceTextView = view.findViewById<TextView>(R.id.tv_current_price)
        val targetPrice = (view.findViewById<ValidatedEditText>(R.id.et_target_price))
        val playerNameTextView = view.findViewById<TextView>(R.id.tv_player_name)
        val psPlatformButton = view.findViewById<Button>(R.id.ps_button)
        val xboxPlatformButton = view.findViewById<Button>(R.id.xbox_button)
        val platform_toggle = view.findViewById<MaterialButtonToggleGroup>(R.id.platform_toggle)
        val gte_lt_toggle = view.findViewById<MaterialButtonToggleGroup>(R.id.gte_lt_toggle)
        val progressBar = view.findViewById<ProgressBar>(R.id.dialog_load_spinner)
        progressBar.visibility = View.VISIBLE

        val vm = ViewModelProvider(requireActivity()).get(SearchPlayerViewModel::class.java)

        vm.selectedPlayer.observe(this, Observer{
            it.getContentIfNotHandled()?.let {
                currentPriceTextView.visibility = View.VISIBLE
                targetPrice.visibility = View.VISIBLE
                playerNameTextView.visibility = View.VISIBLE
                psPlatformButton.visibility = View.VISIBLE
                xboxPlatformButton.visibility = View.VISIBLE
                platform_toggle.visibility = View.VISIBLE
                gte_lt_toggle.visibility = View.VISIBLE
                playerImageView.visibility = View.VISIBLE


                progressBar.visibility = View.GONE

                currentPriceTextView.text = Util.getLocaleFormattedStringFromNumber(it.currentPrice.get(Platform.PS)!!)
                Picasso.get().load(it.imageURL).into(playerImageView)
                playerNameTextView.text = (it.name + " " + it.rating.toString())
                psPlatformButton.setOnClickListener {_ ->
                    currentPriceTextView.text = Util.getLocaleFormattedStringFromNumber(it.currentPrice.get(Platform.PS)!!)
                }
                xboxPlatformButton.setOnClickListener {_ ->
                    currentPriceTextView.text = Util.getLocaleFormattedStringFromNumber(it.currentPrice.get(Platform.XB)!!)
                }


                platform_toggle.addOnButtonCheckedListener { group, checkedId, _ ->
                    if (group.checkedButtonId == -1) group.check(checkedId)
                }

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


                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {_ ->
                    if (targetPrice.isValid and gte_lt_toggle.checkedButtonIds.isNotEmpty()) {
                        val notifiedPlayer = PlayerDBModel()
                        notifiedPlayer.id = it.id
                        notifiedPlayer.name = it.name
                        notifiedPlayer.imageURL = it.imageURL
                        notifiedPlayer.rating = it.rating
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
                        vm.allTrackedPlayers.add(notifiedPlayer)
                        this.dismiss()
                        dialog.cancel()
                    }
                }

            }
        })

        return dialog
    }



}