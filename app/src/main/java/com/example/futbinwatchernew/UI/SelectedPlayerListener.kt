package com.example.futbinwatchernew.UI

import com.example.futbinwatchernew.Network.ResponseModels.SearchPlayerResponse

interface SelectedPlayerListener<T> {
    fun onSearchedPlayerSelected(player:T)
}