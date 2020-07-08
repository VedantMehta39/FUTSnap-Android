package com.example.futbinwatchernew.UI




import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.google.android.material.snackbar.Snackbar
import kotlin.collections.ArrayList

class SwipeToDeleteTrackedPlayerCallback(
    var adapter: TrackedPlayersRecyclerViewAdapter,
    val view: View, var deletedPlayers: ArrayList<PlayerDBModel>
):ItemTouchHelper.SimpleCallback (0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        val deletedItem = adapter.data[position]
        deletedPlayers.add(deletedItem)
        (adapter.data as ArrayList).removeAt(position)
        adapter.notifyDataSetChanged()
        Snackbar.make(view, deletedItem.name+" Deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") { _ ->
                (adapter.data as ArrayList).add(position, deletedItem)
                deletedPlayers.remove(deletedItem)
                adapter.notifyDataSetChanged()
            }
            .show()
    }

}