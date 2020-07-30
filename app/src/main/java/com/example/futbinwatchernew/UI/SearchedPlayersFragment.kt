package com.example.futbinwatchernew.UI

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbinwatchernew.*
import com.example.futbinwatchernew.UI.Models.Platform
import com.example.futbinwatchernew.UI.Models.PlayerDialogFragModel
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import com.example.futbinwatchernew.UI.ErrorHandling.Error
import com.example.futbinwatchernew.UI.Validators.TextLengthValidator
import com.example.futbinwatchernew.UI.ViewModels.SearchPlayerViewModel
import com.example.futbinwatchernew.Utils.*
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SearchedPlayersFragment:Fragment() {

    @Inject
    lateinit var customViewModelFactory: CustomViewModelFactory
    @Inject
    lateinit var clientUtility: ClientUtility


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext()).inflate(R.layout.search_player_recycler_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FUTBINWatcherApp.component.inject(this)

        val vm: SearchPlayerViewModel = ViewModelProvider(this,customViewModelFactory).
        get(SearchPlayerViewModel::class.java)
        val searchFieldLayout = view.findViewById<TextInputLayout>(R.id.search_bar_layout)
        val searchField = view.findViewById<TextInputEditText>(R.id.search_bar)
        val shimmer = requireActivity().findViewById<ShimmerFrameLayout>(R.id.search_shimmer)
        shimmer.setShimmer(Shimmer.AlphaHighlightBuilder().setAutoStart(false).build())

        vm.error.observe(viewLifecycleOwner, Observer {errorEvent ->
            errorEvent.getContentIfNotHandled()?.let {
                    error ->
                shimmer.stopShimmer()
                shimmer.visibility = View.GONE
                when(error){
                    is Error.GeneralError -> {
                        DynamicToast.makeError(requireContext(), error.message,Toast.LENGTH_LONG).show()
                    }
                    is Error.RegistrationError ->{
                        parentFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container_view_tag,
                                ErrorFragment(error, clientUtility::addOrUpdateTokenOnServer), "ERROR_FRAG"
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
            }
        })

        searchField.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int,
                                               lengthAfter: Int) {
                text?.let {
                    val validator = TextLengthValidator(3,null)
                    if(!validator.validate(it.toString())){
                        searchFieldLayout.error = validator.errorMessage
                    }
                    else{
                        searchFieldLayout.error = null
                    }
                }
            }

        })
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = SearchPlayersRecyclerViewAdapter(
            ArrayList(),
             object:SelectedPlayerListener<SearchPlayerResponse>{
                override fun onSearchedPlayerSelected(player: SearchPlayerResponse) {
                    if (parentFragmentManager.findFragmentByTag("PLAYER_DIALOG_FRAG") == null){
                        val data = PlayerDialogFragModel(player.id,
                            player.playerName+ " " +player.playerRating.toString(),
                            player.playerImage,
                            EnumMap<Platform,Int?>(Platform::class.java),
                            null,Platform.PS, gte = false, lt = true, isEdited = false)
                        SinglePlayerDialog(data).show(parentFragmentManager,"PLAYER_DIALOG_FRAG")
                    }
                }

            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        vm.searchPlayersResult.observe(viewLifecycleOwner, Observer {
            shimmer.stopShimmer()
            shimmer.visibility = View.GONE
            adapter.updateData(it)
            if(it.isEmpty()){
                val toast = Toast.makeText(requireContext(),"No players found",Toast.LENGTH_SHORT)
                toast.show()
            }
        })

        searchField.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                shimmer.visibility = View.VISIBLE
                shimmer.startShimmer()
                vm.getSearchPlayerResults(20, textView.text.toString())
            }
            return@setOnEditorActionListener true
        }

    }

}