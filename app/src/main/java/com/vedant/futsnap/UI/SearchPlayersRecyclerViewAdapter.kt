package com.vedant.futsnap.UI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vedant.futsnap.Network.ResponseModels.SearchPlayerResponse
import com.vedant.futsnap.R
import com.squareup.picasso.Picasso

class SearchPlayersRecyclerViewAdapter(var data:ArrayList<SearchPlayerResponse>, var selectedPlayerListener:SelectedPlayerListener<SearchPlayerResponse>):RecyclerView.Adapter<SearchPlayersRecyclerViewAdapter.MyViewHolder>(){
    inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val playerNameTextView: TextView = view.findViewById<TextView>(R.id.tv_player_name)
        val playerRatingTextView: TextView = view.findViewById<TextView>(R.id.tv_player_rating)
        val playerImageView: ImageView = view.findViewById<ImageView>(R.id.img_player)
        fun bindData(data:SearchPlayerResponse){
            playerNameTextView.text = data.playerName
            playerRatingTextView.text = data.playerRating.toString()
            Picasso.get().load(data.playerImage).into(playerImageView)
        }

    }

    fun updateData(newData:List<SearchPlayerResponse>){
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_player_view,parent,false)
        val holder =  MyViewHolder(view)
        holder.playerNameTextView.setOnClickListener {
            selectedPlayerListener.onSearchedPlayerSelected(data[holder.adapterPosition])
        }
        return holder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(data[position])
    }
}