package com.example.futbinwatchernew.UI

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.*
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

    lateinit var playerImageView:ImageView
    lateinit var currentPriceTextView:TextView
    lateinit var targetPrice:ValidatedEditText
    lateinit var playerNameTextView:TextView
    lateinit var psPlatformButton:Button
    lateinit var xboxPlatformButton:Button
    lateinit var platform_toggle:MaterialButtonToggleGroup
    lateinit var gte_lt_toggle:MaterialButtonToggleGroup
    lateinit var progressBar:ProgressBar
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
        val view = requireActivity().layoutInflater.inflate(R.layout.single_player_dialog, null)
        val vm = ViewModelProvider(requireActivity()).get(SearchPlayerViewModel::class.java)

        val dialog = getBaseDialog(view)
        dialog.show()

        initViews(view)
        showLoadingSpinner(true)

        vm.selectedPlayer.observe(this, Observer{ event ->
            event.getContentIfNotHandled()?.let {
                if(it.currentPrice[Platform.PS] == null || it.currentPrice[Platform.XB] == null){
                    val toast = Toast.makeText(requireContext(),
                        "Player price couldn't be fetched. Try again later",Toast.LENGTH_LONG)
                    toast.show()
                    dialog.cancel()
                }
                else{
                    showLoadingSpinner(false)

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


                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {_ ->
                        if (targetPrice.isValid and gte_lt_toggle.checkedButtonIds.isNotEmpty()) {
                            val notifiedPlayer = PlayerDBModel()
                            notifiedPlayer.futbinId = it.id
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
                            dialog.cancel()
                        }
                    }
                }
            }
        })
        return dialog
    }

    private fun initViews(view:View){
        playerImageView = view.findViewById<ImageView>(R.id.img_player)
        currentPriceTextView = view.findViewById<TextView>(R.id.tv_current_price)

        targetPrice = (view.findViewById<ValidatedEditText>(R.id.et_target_price))
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

        playerNameTextView = view.findViewById<TextView>(R.id.tv_player_name)
        psPlatformButton = view.findViewById<Button>(R.id.ps_button)
        xboxPlatformButton = view.findViewById<Button>(R.id.xbox_button)
        platform_toggle = view.findViewById<MaterialButtonToggleGroup>(R.id.platform_toggle)
        gte_lt_toggle = view.findViewById<MaterialButtonToggleGroup>(R.id.gte_lt_toggle)
        progressBar = view.findViewById<ProgressBar>(R.id.dialog_load_spinner)
    }

    private fun showLoadingSpinner(show:Boolean){
        val progressBarVisibility:Int
        val otherVisibility:Int
        when(show){
            true -> {
                progressBarVisibility = View.VISIBLE
                otherVisibility = View.GONE
            }
            false ->{
                progressBarVisibility = View.GONE
                otherVisibility = View.VISIBLE
            }
        }
        progressBar.visibility = progressBarVisibility
        currentPriceTextView.visibility = otherVisibility
        targetPrice.visibility = otherVisibility
        playerNameTextView.visibility = otherVisibility
        psPlatformButton.visibility = otherVisibility
        xboxPlatformButton.visibility = otherVisibility
        platform_toggle.visibility = otherVisibility
        gte_lt_toggle.visibility = otherVisibility
        playerImageView.visibility = otherVisibility

    }

    private fun getBaseDialog(view: View):AlertDialog{
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(view).setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }.setPositiveButton(R.string.ok,null)
        return builder.create()
    }


}