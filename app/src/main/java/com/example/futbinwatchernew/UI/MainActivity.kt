package com.example.futbinwatchernew.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.futbinwatchernew.*
import com.example.futbinwatchernew.DI.DaggerApplicationComponent
import com.example.futbinwatchernew.DI.DatabaseModule
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){

            val vm = ViewModelProvider(this).get(SearchPlayerViewModel::class.java)
            FUTBINWatcherApp.component.get("SEARCH")!!.inject(vm)
            vm.getTrackedPlayersList()
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container_view_tag,
                    SearchedPlayersFragment()
                )
                .commit()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.player_list, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val frag = TrackedPlayersFragment.newInstance()
        if(item.itemId == R.id.players_list_button){
            if (!frag.isAdded){
                setContentView(R.layout.tracked_players_fragment_container)
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container_view_tag,
                        frag, "TRACKED_PLAYERS_FRAG"
                    ).addToBackStack(null)
                    .commit()

                }
            }
        return true
    }

    override fun onStop() {
        val vm = ViewModelProvider(this).get(SearchPlayerViewModel::class.java)
        if(vm.isTrackedPlayersInDBInitialized()){
            while (vm.deletedPlayersStack.isNotEmpty()){
                val deletedItem = vm.deletedPlayersStack.pop()
                vm.deletePlayer(deletedItem)
            }
            vm.insert(vm.allTrackedPlayers)
        }
        return super.onStop()
    }


}
