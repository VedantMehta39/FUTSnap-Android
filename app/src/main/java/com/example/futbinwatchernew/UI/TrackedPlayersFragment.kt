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
import com.example.futbinwatchernew.MainActivityViewModel
import com.example.futbinwatchernew.UI.Models.Platform
import com.example.futbinwatchernew.UI.Models.PlayerDialogFragModel
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.Network.ResponseModels.PlayerTrackingRequest
import java.util.*

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
        val mainActivityVm = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        val recyclerView =  view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        var adapter: TrackedPlayersRecyclerViewAdapter
        if(!mainActivityVm.allPlayerTrackingRequests.hasActiveObservers()){
            mainActivityVm.allPlayerTrackingRequests.observe(requireActivity(), Observer {

                adapter = TrackedPlayersRecyclerViewAdapter(it,
                object :SelectedPlayerListener<PlayerTrackingRequest>{
                    override fun onSearchedPlayerSelected(player: PlayerTrackingRequest) {
                        if (parentFragmentManager.findFragmentByTag("PLAYER_DIALOG_FRAG") == null){
                            val data = PlayerDialogFragModel(player.PlayerId,player.Player!!.CardName,
                                player.Player!!.ImageUrl, EnumMap<Platform,Int?>(Platform::class.java),
                                player.TargetPrice, Platform.values()[player.Platform], player.Gte,
                                player.Lt, true)
                            SinglePlayerDialog(data).show(parentFragmentManager,"PLAYER_DIALOG_FRAG")
                        }
                }
                })
                recyclerView.adapter = adapter
                val itemTouchHelper = ItemTouchHelper(SwipeToDeleteTrackedPlayerCallback(adapter,recyclerView, mainActivityVm.deletedTrackedPlayers))
                itemTouchHelper.attachToRecyclerView(recyclerView)
            })
        }
        else{
            adapter = TrackedPlayersRecyclerViewAdapter(mainActivityVm.allPlayerTrackingRequests.value!!,
                object :SelectedPlayerListener<PlayerTrackingRequest>{
                    override fun onSearchedPlayerSelected(player: PlayerTrackingRequest) {
                        if (parentFragmentManager.findFragmentByTag("PLAYER_DIALOG_FRAG") == null){
                            val data = PlayerDialogFragModel(player.PlayerId,player.Player!!.CardName,
                                player.Player!!.ImageUrl, EnumMap<Platform,Int?>(Platform::class.java),
                                player.TargetPrice, Platform.values()[player.Platform], player.Gte,
                            player.Lt, true)
                            SinglePlayerDialog(data).show(parentFragmentManager,"PLAYER_DIALOG_FRAG")
                        }
                    }

                })
            recyclerView.adapter = adapter
            val itemTouchHelper = ItemTouchHelper(SwipeToDeleteTrackedPlayerCallback(adapter,recyclerView, mainActivityVm.deletedTrackedPlayers))
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }

    }

}