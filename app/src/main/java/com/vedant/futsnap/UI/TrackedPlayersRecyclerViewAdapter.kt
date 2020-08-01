package com.vedant.futsnap.UI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vedant.futsnap.UI.Models.Platform
import com.vedant.futsnap.R
import com.vedant.futsnap.Network.ResponseModels.PlayerTrackingRequest
import com.vedant.futsnap.Utils.StringFormatter
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso

class TrackedPlayersRecyclerViewAdapter(var data: ArrayList<PlayerTrackingRequest>, val selectedPlayerListener: SelectedPlayerListener<PlayerTrackingRequest>):RecyclerView.Adapter<TrackedPlayersRecyclerViewAdapter.MyViewHolder>(){
    inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val trackedPlayerCard: CardView = view.findViewById<CardView>(R.id.player_card)
        val playerNameTextView: TextView = view.findViewById<TextView>(R.id.tv_player_name)
        val playerImageView: ImageView = view.findViewById<ImageView>(R.id.img_player)
        val playerTargetPriceView: TextInputEditText = view.findViewById<TextInputEditText>(R.id.et_target_price)
        val platformImageView: ImageView = view.findViewById<ImageView>(R.id.platform_icon)
        val gte_lt_toggle: MaterialButtonToggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.gte_lt_toggle)
        lateinit var request: PlayerTrackingRequest
        fun bindData(data: PlayerTrackingRequest){
            request = data
            playerNameTextView.text = (data.Player!!.CardName)
            playerTargetPriceView.setText(StringFormatter.getLocaleFormattedStringFromNumber(data.TargetPrice))
            when(Platform.values()[data.Platform]){
                Platform.PS -> platformImageView.setImageResource(R.drawable.ic_icons8_playstation)
                Platform.XB -> platformImageView.setImageResource(R.drawable.ic_icons8_xbox)
            }
            if(data.Gte){
                gte_lt_toggle.check(R.id.gte_target)
            }
            if(data.Lt){
                gte_lt_toggle.check(R.id.lt_target)
            }

            Picasso.get().load(data.Player!!.ImageUrl).into(playerImageView)
        }
    }

    fun updateData(newData:List<PlayerTrackingRequest>){
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracked_player,parent,false)
        val holder = MyViewHolder(view)
        holder.trackedPlayerCard.setOnClickListener{
            selectedPlayerListener.onSearchedPlayerSelected(holder.request)
        }
        return holder

    }

    override fun getItemCount(): Int {
        if(data.isNullOrEmpty()){
            return 0
        }
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(data[position])
    }
}