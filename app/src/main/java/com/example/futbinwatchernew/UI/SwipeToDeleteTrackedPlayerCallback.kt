package com.example.futbinwatchernew.UI




import android.view.View
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.futbinwatchernew.Utils.NetworkResponse
import com.google.android.material.snackbar.Snackbar

class SwipeToDeleteTrackedPlayerCallback(
    var adapter: TrackedPlayersRecyclerViewAdapter,
    private val view: View, private val owner:LifecycleOwner, private val deleteFromServer: (playerId:Int) -> LiveData<NetworkResponse<Int>>
):ItemTouchHelper.SimpleCallback (0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        val deletedItem = adapter.data[position]

        deleteFromServer.invoke(deletedItem.PlayerId).observe(owner,Observer{response ->
            when(response){
                is NetworkResponse.Success ->{
                    Snackbar.make(view, deletedItem.Player!!.CardName+" Deleted",
                        Snackbar.LENGTH_LONG).show()
                }
                is NetworkResponse.Failure ->{
                    Snackbar.make(view, deletedItem.Player!!.CardName+" could not be deleted." +
                            " Please try again later!", Snackbar.LENGTH_LONG).show()
                }
            }
        })

    }

}