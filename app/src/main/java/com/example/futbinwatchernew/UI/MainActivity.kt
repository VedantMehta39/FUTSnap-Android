package com.example.futbinwatchernew.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.futbinwatchernew.*
import com.example.futbinwatchernew.Network.ResponseModels.Client
import com.example.futbinwatchernew.Utils.Error
import com.example.futbinwatchernew.Utils.ErrorType
import com.example.futbinwatchernew.Utils.SharedPrefFileNames
import com.example.futbinwatchernew.Utils.SharedPrefRepo
import com.example.futbinwatchernew.Utils.SharedPrefsTags
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){
            val sharedPrefRepo = SharedPrefRepo(this, SharedPrefFileNames.CLIENT_REGISTRATION)
            val firebaseToken = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.FIREBASE_TOKEN_KEY) as String?
            // CASE: FirebaseToken updated in background
            if(firebaseToken != null){
                // Retrieve the token from SharedPref, if not synced then sync on serve
                if(!(sharedPrefRepo.readFromSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC) as Boolean)){
                    addOrUpdateTokenOnServer(firebaseToken)
                }

            }
            else{
                // CASE: FirebaseToken updated in foreground
                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                    addOrUpdateTokenOnServer(it.token)
                }
            }



            val defaultClientId = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.CLIENT_ID) as Int
            val vm = ViewModelProvider(this,CustomViewModelFactory(defaultClientId)).
            get(MainActivityViewModel::class.java)
            FUTBINWatcherApp.component["SERVICE"]!!.inject(vm)
            vm.clientId.observe(this, Observer {
                sharedPrefRepo.writeToSharedPref(SharedPrefsTags.CLIENT_ID, it)
                sharedPrefRepo.writeToSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC, true)
                vm.getPlayerTrackingRequests()
            })

            vm.error.observe(this, Observer {error ->

                when(error){
                    is Error.GeneralError -> {
                        Toast.makeText(this, error.message,Toast.LENGTH_LONG).show()
                    }
                    is Error.RegistrationError ->{
                        sharedPrefRepo.writeToSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC, false)
                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container_view_tag,
                                ErrorFragment(error) {
                                    addOrUpdateTokenOnServer(sharedPrefRepo.readFromSharedPref(SharedPrefsTags.FIREBASE_TOKEN_KEY) as String)
                                }, "ERROR_FRAG"
                            ).commit()
                    }
                    is Error.ServerError ->{
                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container_view_tag,
                                ErrorFragment(error,null), "ERROR_FRAG"
                            ).commit()
                    }

                }



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
                vm.deletePlayerTrackingRequest(deletedRequest.PlayerId)
                vm.deletedTrackedPlayers.pop()
            }
            catch (e:Exception){

            }
        }
        return super.onStop()
    }


    private fun addOrUpdateTokenOnServer(newToken:String){
        val vm = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        val sharedPrefRepo = SharedPrefRepo(this, SharedPrefFileNames.CLIENT_REGISTRATION)
        val clientId = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.CLIENT_ID) as Int
        CoroutineScope(Dispatchers.IO).launch {
            if(clientId == -1){
                vm.addClient(Client(0,newToken))
            }
            else{
                vm.editClient(Client(clientId,newToken))
            }
        }


    }



}
