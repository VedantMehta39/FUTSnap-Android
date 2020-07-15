package com.example.futbinwatchernew.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.futbinwatchernew.*
import com.example.futbinwatchernew.Utils.SharedPrefFileNames
import com.example.futbinwatchernew.Utils.SharedPrefRepo
import com.example.futbinwatchernew.Utils.SharedPrefsTags
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){


            val vm = ViewModelProvider(this).get(MainActivityViewModel::class.java)
            FUTBINWatcherApp.component["SERVICE"]!!.inject(vm)
            vm.clientId = SharedPrefRepo(this,
                SharedPrefFileNames.CLIENT_REGISTRATION)
                .readFromSharedPref(SharedPrefsTags.CLIENT_ID) as Int
            vm.getPlayerTrackingRequests()

            vm.errorMessage.observe(this, Observer {
                Toast.makeText(this,it!!,Toast.LENGTH_SHORT).show()
            })


            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container_view_tag,
                    SearchedPlayersFragment()
                )
                .commit()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.player_list, menu)
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
        val vm = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        while (vm.deletedTrackedPlayers.isNotEmpty()){
            val deletedRequest = vm.deletedTrackedPlayers.peek()
            try{
                vm.deletePlayerTrackingRequest(deletedRequest.PlayerId, deletedRequest.ClientId)
                vm.deletedTrackedPlayers.pop()
            }
            catch (e:Exception){

            }
        }
        return super.onStop()
    }



}
