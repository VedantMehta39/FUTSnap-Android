package com.example.futbinwatchernew.UI




import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.futbinwatchernew.Network.ResponseModels.PlayerTrackingRequest
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

class SwipeToDeleteTrackedPlayerCallback(
    var adapter: TrackedPlayersRecyclerViewAdapter,
    val view: View, var deletedPlayers: Stack<PlayerTrackingRequest>
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
        deletedPlayers.add(deletedItem)
        (adapter.data as ArrayList).removeAt(position)
        adapter.notifyDataSetChanged()
        Snackbar.make(view, deletedItem.Player!!.CardName+" Deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") { _ ->
                (adapter.data as ArrayList).add(position, deletedItem)
                deletedPlayers.remove(deletedItem)
                adapter.notifyDataSetChanged()
            }
            .show()
    }

}