package com.example.futbinwatchernew.UI

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.futbinwatchernew.*
import com.example.futbinwatchernew.UI.Models.Platform
import com.example.futbinwatchernew.UI.Models.PlayerDialogFragModel
import com.example.futbinwatchernew.Network.ResponseModels.Player
import com.example.futbinwatchernew.Network.ResponseModels.PlayerTrackingRequest
import com.example.futbinwatchernew.UI.Validators.TextContentValidator
import com.example.futbinwatchernew.UI.Validators.TextLengthValidator
import com.example.futbinwatchernew.Utils.*
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso


class SinglePlayerDialog(val data:PlayerDialogFragModel):DialogFragment() {

    lateinit var playerImageView:ImageView
    lateinit var currentPriceTextView:TextView
    lateinit var targetPrice:TextInputEditText
    lateinit var playerNameTextView:TextView
    lateinit var psPlatformButton:Button
    lateinit var xboxPlatformButton:Button
    lateinit var platform_toggle:MaterialButtonToggleGroup
    lateinit var gte_lt_toggle:MaterialButtonToggleGroup
    lateinit var progressBar:ProgressBar

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.single_player_dialog, null)
        val dialog = getBaseDialog(view)
        dialog.show()

        val vm = ViewModelProvider(this).get(SinglePlayerDialogFragmentViewModel::class.java)
        FUTBINWatcherApp.component["PRICE"]!!.inject(vm)
        vm.initSelectedPlayer(data)

        vm.error.observe(this, Observer {error ->

            when(error){
                is Error.GeneralError -> {
                    Toast.makeText(requireContext(), error.message,Toast.LENGTH_LONG).show()
                    dismiss()
                }
                is Error.RegistrationError ->{
                    val sharedPrefRepo = SharedPrefRepo(requireActivity(),
                        SharedPrefFileNames.CLIENT_REGISTRATION)
                    sharedPrefRepo.writeToSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC, false)
                    parentFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container_view_tag,
                            ErrorFragment(error,null), "ERROR_FRAG"
                        ).commit()
                }
                is Error.ServerError ->{
                    parentFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container_view_tag,
                            ErrorFragment(error,null), "ERROR_FRAG"
                        ).commit()
                }

            }



        })

        initViews(view)
        showLoadingSpinner(true)

        vm.selectedPlayer.observe(this, Observer{ event ->
            event.getContentIfNotHandled()?.let {
                    showLoadingSpinner(false)

                    playerNameTextView.text = (it.cardName)
                    targetPrice.setText(it.targetPrice?.toString())

                    if(it.gte){
                        gte_lt_toggle.check(R.id.gte_target)
                    }
                    else{
                        gte_lt_toggle.check(R.id.lt_target)
                    }
                    when(it.platform){
                        Platform.PS ->{
                            platform_toggle.check(R.id.ps_button)
                            currentPriceTextView.text = StringFormatter.getLocaleFormattedStringFromNumber(
                                it.currentPrice[Platform.PS]!!)
                        }
                        Platform.XB ->{
                            platform_toggle.check(R.id.xbox_button)
                            currentPriceTextView.text = StringFormatter.getLocaleFormattedStringFromNumber(
                                it.currentPrice[Platform.XB]!!)
                        }
                    }
                    Picasso.get().load(it.imageURL).into(playerImageView)


                    psPlatformButton.setOnClickListener {_ ->
                        currentPriceTextView.text = StringFormatter.getLocaleFormattedStringFromNumber(
                            it.currentPrice[Platform.PS]!!)
                    }
                    xboxPlatformButton.setOnClickListener {_ ->
                        currentPriceTextView.text = StringFormatter.getLocaleFormattedStringFromNumber(
                            it.currentPrice[Platform.XB]!!)
                    }

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {_ ->
                        val mainActivityVm = ViewModelProvider(requireActivity())
                                                .get(MainActivityViewModel::class.java)

                        val contentValidator = TextContentValidator("-.")
                        val lengthValidator = TextLengthValidator(1,null)

                        if (lengthValidator.validate(targetPrice.text.toString()) and
                            contentValidator.validate(targetPrice.text.toString())) {
                            val notifiedPlayer =
                                PlayerTrackingRequest()
                            notifiedPlayer.PlayerId = it.id
                            notifiedPlayer.Player =
                                Player(
                                    it.id,
                                    it.cardName,
                                    it.imageURL
                                )
                            val chosenPlatformId = platform_toggle.checkedButtonId
                            val chosenComparisonDirectionIds = gte_lt_toggle.checkedButtonIds
                            when(chosenPlatformId){
                                R.id.ps_button -> notifiedPlayer.Platform = Platform.PS.ordinal
                                R.id.xbox_button -> notifiedPlayer.Platform = Platform.XB.ordinal
                            }
                            if(chosenComparisonDirectionIds.contains(R.id.gte_target)){
                                notifiedPlayer.Gte = true
                            }
                            if(chosenComparisonDirectionIds.contains(R.id.lt_target)){
                                notifiedPlayer.Lt = true
                            }
                            notifiedPlayer.TargetPrice = targetPrice.text.toString().toInt()
                            notifiedPlayer.ClientId = mainActivityVm.clientId.value!!

                            if(it.isEdited){
                                mainActivityVm.editPlayerTrackingRequest(notifiedPlayer.PlayerId,notifiedPlayer)
                            }
                            else{
                                mainActivityVm.addPlayerTrackingRequest(notifiedPlayer)
                            }
                            mainActivityVm.requestSuccessful.observe(this, Observer { requestSuccessful ->
                                if(requestSuccessful){
                                    Toast.makeText(requireContext(),"Player Tracking Request" +
                                            " added successfully!",Toast.LENGTH_SHORT).show()
                                    dismiss()
                                }
                            })
                        }
                    }
                }
        })
        return dialog
    }

    private fun initViews(view:View){
        playerImageView = view.findViewById<ImageView>(R.id.img_player)
        currentPriceTextView = view.findViewById<TextView>(R.id.tv_current_price)

        targetPrice = (view.findViewById<TextInputEditText>(R.id.et_target_price))
        targetPrice.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text?.let {
                    val validator = TextContentValidator("-.")
                    if(!validator.validate(text.toString())){
                        targetPrice.error = validator.errorMessage
                    }
                }
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
            dismiss()
        }.setPositiveButton(R.string.ok,null)
        return builder.create()
    }


}