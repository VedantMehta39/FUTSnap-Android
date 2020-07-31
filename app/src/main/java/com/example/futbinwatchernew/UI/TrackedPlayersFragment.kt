package com.example.futbinwatchernew.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbinwatchernew.FUTBINWatcherApp
import com.example.futbinwatchernew.UI.ViewModels.MainActivityViewModel
import com.example.futbinwatchernew.UI.Models.Platform
import com.example.futbinwatchernew.UI.Models.PlayerDialogFragModel
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.Network.ResponseModels.PlayerTrackingRequest
import com.example.futbinwatchernew.UI.ErrorHandling.ErrorHandling
import com.example.futbinwatchernew.UI.ViewModels.CustomViewModelFactory
import com.example.futbinwatchernew.Utils.*
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class TrackedPlayersFragment: Fragment() {

    @Inject
    lateinit var customViewModelFactory: CustomViewModelFactory
    @Inject
    lateinit var clientUtility: ClientUtility

    companion object{
        private var fragment:TrackedPlayersFragment? = null
        fun newInstance():TrackedPlayersFragment{
            if(fragment == null){
                fragment = TrackedPlayersFragment()
                FUTBINWatcherApp.component.inject(fragment!!)
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
        val mainActivityVm = ViewModelProvider(requireActivity(),customViewModelFactory).get(
            MainActivityViewModel::class.java)
        val recyclerView =  view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TrackedPlayersRecyclerViewAdapter(
            ArrayList(),
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
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteTrackedPlayerCallback(adapter,
            recyclerView, viewLifecycleOwner,
            mainActivityVm::deletePlayerTrackingRequest))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        if(mainActivityVm.allPlayerTrackingRequests.value == null){
            mainActivityVm.getPlayerTrackingRequests().observe(viewLifecycleOwner, Observer {response ->
                when(response){
                    is NetworkResponse.Success ->{
                        mainActivityVm.allPlayerTrackingRequests.value = response.data
                    }
                    is NetworkResponse.Failure ->{
                        val errorHandler = ErrorHandling(requireContext(),
                            parentFragmentManager, clientUtility::addOrUpdateTokenOnServer)
                        errorHandler.handle(response.error)
                    }
                }
            })
        }

        if(!mainActivityVm.allPlayerTrackingRequests.hasActiveObservers()){
            mainActivityVm.allPlayerTrackingRequests.observe(viewLifecycleOwner, Observer {
                if(it.isEmpty()){
                    DynamicToast.make(requireContext(), "You have no player tracking requests!",
                        Toast.LENGTH_LONG).show()
                }
                else{
                    adapter.updateData(it)
                }
            })
        }
    }

}