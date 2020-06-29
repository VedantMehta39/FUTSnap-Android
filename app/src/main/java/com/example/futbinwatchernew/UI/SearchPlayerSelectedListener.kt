package com.example.futbinwatchernew.UI

import androidx.lifecycle.LiveData
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse

interface SearchPlayerSelectedListener {
    fun onSearchedPlayerSelected(player:SearchPlayerResponse)
}