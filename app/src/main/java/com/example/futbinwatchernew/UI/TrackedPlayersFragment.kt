package com.example.futbinwatchernew.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.FUTBINWatcherApp
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.SearchPlayerViewModel
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout

class TrackedPlayersFragment: Fragment() {

    companion object{
        var fragment:TrackedPlayersFragment? = null
        fun newInstance():TrackedPlayersFragment{
            if(fragment == null){
                fragment = TrackedPlayersFragment()
            }
            return fragment!!
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LayoutInflater.from(container!!.context).inflate(R.layout.tracked_players_recycler, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm = ViewModelProvider(requireActivity()).get(SearchPlayerViewModel::class.java)
        FUTBINWatcherApp.component.get("SEARCH")!!.inject(vm)
        val recyclerView =  view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        var adapter: TrackedPlayersRecyclerViewAdapter
        if(!vm.trackedPlayersInDb.hasActiveObservers()){
            vm.trackedPlayersInDb.observe(requireActivity(), Observer {

                vm.allTrackedPlayers = vm.allTrackedPlayers.union(it).toMutableList() as ArrayList<PlayerDBModel>
                adapter = TrackedPlayersRecyclerViewAdapter(vm.allTrackedPlayers)
                recyclerView.adapter = adapter
                val itemTouchHelper = ItemTouchHelper(SwipeToDeleteTrackedPlayerCallback(adapter,recyclerView, vm.deletedPlayersStack))
                itemTouchHelper.attachToRecyclerView(recyclerView)
            })
        }
        else{
            adapter = TrackedPlayersRecyclerViewAdapter(vm.allTrackedPlayers)
            recyclerView.adapter = adapter
            val itemTouchHelper = ItemTouchHelper(SwipeToDeleteTrackedPlayerCallback(adapter,recyclerView, vm.deletedPlayersStack))
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }

    }

}