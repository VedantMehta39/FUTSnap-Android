package com.example.futbinwatchernew.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.futbinwatchernew.*
import com.example.futbinwatchernew.UI.ViewModels.CustomViewModelFactory
import com.example.futbinwatchernew.UI.ViewModels.MainActivityViewModel
import com.example.futbinwatchernew.Utils.*
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
            val defaultClientId = sharedPrefRepo.readFromSharedPref(SharedPrefsTags.CLIENT_ID) as Int
            val vm = ViewModelProvider(this, customViewModelFactory).get(MainActivityViewModel::class.java)
            vm.clientId = defaultClientId

            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container_view_tag,
                    SearchedPlayersFragment()
                )
                .commit()

            when(intent.getStringExtra("TO")){
                "NOTIFICATION_PROBLEM_INFO_FRAGMENT" -> {
                    NotificationsProblemInfoFragment().show(supportFragmentManager,"INFO_FRAG")
                }
                "TRACKED_PLAYERS_FRAGMENT" ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view_tag,
                            TrackedPlayersFragment.newInstance()).addToBackStack(null)
                        .commit()
                }
            }
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
            else{
                supportFragmentManager.popBackStack()
            }
        }
        return true
    }

}
