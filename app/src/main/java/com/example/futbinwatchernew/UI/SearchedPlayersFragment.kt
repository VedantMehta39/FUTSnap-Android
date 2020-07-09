package com.example.futbinwatchernew.UI

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbinwatchernew.*
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
import com.example.futbinwatchernew.UI.Validators.TextLengthValidator
import com.example.futbinwatchernew.UI.Validators.Validator
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout

class SearchedPlayersFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext()).inflate(R.layout.search_player_recycler_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm = ViewModelProvider(requireActivity()).get(SearchPlayerViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
        val searchButton = view.findViewById<ImageButton>(R.id.enter)
        val searchField = view.findViewById<EditText>(R.id.searchBar)
        val shimmer = requireActivity().findViewById<ShimmerFrameLayout>(R.id.search_shimmer)
        shimmer.setShimmer(Shimmer.AlphaHighlightBuilder().setAutoStart(false).build())
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
                        searchField.error = validator.errorMessage
                    }
                }
            }

        })
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = SearchPlayersRecyclerViewAdapter(
            emptyList(),
            parentFragmentManager,
            object:SearchPlayerSelectedListener{
                override fun onSearchedPlayerSelected(player: SearchPlayerResponse) {
                    FUTBINWatcherApp.component["PRICE"]!!.inject(vm)
                    vm.initSelectedPlayer(player)
                    if (parentFragmentManager.findFragmentByTag("PLAYER_DIALOG_FRAG") == null){
                        SinglePlayerDialog.newInstance().show(parentFragmentManager,"PLAYER_DIALOG_FRAG")
                    }
                }

            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        vm.searchPlayersResult.observe(viewLifecycleOwner, Observer {
            shimmer.stopShimmer()
            shimmer.visibility = View.GONE
            adapter.data = it
            adapter.notifyDataSetChanged()
            if(it.isEmpty()){
                val toast = Toast.makeText(requireContext(),"No players found",Toast.LENGTH_SHORT)
                toast.show()
            }
        })

        searchButton.setOnClickListener{
            FUTBINWatcherApp.component["SEARCH"]!!.inject(vm)
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            vm.getSearchPlayerResults(20, searchField.text.toString())
        }

    }

}