package com.example.futbinwatchernew.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbinwatchernew.*
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.Models.PlayerDialogFragModel
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse
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
        val searchField = view.findViewById<ValidatedEditText>(R.id.searchBar)
        val shimmer = requireActivity().findViewById<ShimmerFrameLayout>(R.id.search_shimmer)
        shimmer.setShimmer(Shimmer.AlphaHighlightBuilder().setAutoStart(false).build())
        searchField.setValidator(object :
            Validator {
            override var errorMessage: String = "Search Term must be of atleast 3 letters"
                get() = field

            override fun validate(data: String): Boolean {
                if (data.length< 3){
                    return false
                }
                return true
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
                    vm.selectedPlayer.observe(viewLifecycleOwner,
                        Observer<Event<PlayerDialogFragModel>> { event ->
                            event.getContentIfNotHandled()?.let {
                                SinglePlayerDialog.newInstance(it).show(parentFragmentManager,null)
                            }
                        })
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
        })

        searchButton.setOnClickListener{
            FUTBINWatcherApp.component.get("SEARCH")!!.inject(vm)
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
            vm.getSearchPlayerResults(20, searchField.text.toString())
        }

    }

}