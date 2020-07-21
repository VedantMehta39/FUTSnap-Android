package com.example.futbinwatchernew.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.futbinwatchernew.*
import com.example.futbinwatchernew.UI.ErrorHandling.ErrorHandling
import com.example.futbinwatchernew.UI.ViewModels.MainActivityViewModel
import com.example.futbinwatchernew.Utils.*
import com.google.firebase.iid.FirebaseInstanceId
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var customViewModelFactory: CustomViewModelFactory
    @Inject
    lateinit var clientUtility: ClientUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FUTBINWatcherApp.component.inject(this)
        if(savedInstanceState == null){
            val sharedPrefRepo = SharedPrefRepo(this, SharedPrefFileNames.CLIENT_REGISTRATION)
            val firebaseToken = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.FIREBASE_TOKEN_KEY) as String?

            val defaultClientId = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.CLIENT_ID) as Int
            val vm = ViewModelProvider(this, customViewModelFactory).get(MainActivityViewModel::class.java)
            vm.clientId = defaultClientId

            // CASE: FirebaseToken updated in background
            if(firebaseToken != null){
                // Retrieve the token from SharedPref, if not synced then sync on serve
                if(!(sharedPrefRepo.readFromSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC) as Boolean)){
                    clientUtility.addOrUpdateTokenOnServer(this,firebaseToken).observe(this,
                        Observer {response ->
                            when (response) {
                                is NetworkResponse.Success -> {
                                    sharedPrefRepo.writeToSharedPref(SharedPrefsTags.CLIENT_ID, response.data)
                                    sharedPrefRepo.writeToSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC, true)
                                    vm.clientId = response.data
                                }
                                is NetworkResponse.Failure -> ErrorHandling(this,
                                    supportFragmentManager, clientUtility::addOrUpdateTokenOnServer)
                            }
                        })
                }

            }
            else{
                // CASE: FirebaseToken updated in foreground
                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                    clientUtility.addOrUpdateTokenOnServer(this,it.token).observe(this, Observer {response ->
                        when (response) {
                            is NetworkResponse.Success -> {
                                sharedPrefRepo.writeToSharedPref(SharedPrefsTags.CLIENT_ID, response.data)
                                sharedPrefRepo.writeToSharedPref(SharedPrefsTags.IS_DATABASE_IN_SYNC, true)
                                vm.clientId = response.data
                            }
                            is NetworkResponse.Failure -> ErrorHandling(this,
                                supportFragmentManager, clientUtility::addOrUpdateTokenOnServer)
                        }
                    })
                }
            }


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

}
