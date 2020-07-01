package com.example.futbinwatchernew.UI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.Models.Platform
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.Util
import com.google.android.material.button.MaterialButtonToggleGroup
import com.squareup.picasso.Picasso

class TrackedPlayersRecyclerViewAdapter(var data: List<PlayerDBModel>):RecyclerView.Adapter<TrackedPlayersRecyclerViewAdapter.MyViewHolder>(){
    inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val playerNameTextView = view.findViewById<TextView>(R.id.tv_player_name)
        val playerImageView = view.findViewById<ImageView>(R.id.img_player)
        val playerTargetPriceView = view.findViewById<ValidatedEditText>(R.id.et_target_price)
        val platformImageView = view.findViewById<ImageView>(R.id.platform_icon)
        val gte_lt_toggle = view.findViewById<MaterialButtonToggleGroup>(R.id.gte_lt_toggle)
        fun bindData(data: PlayerDBModel){
            playerNameTextView.text = (data.name + " " + data.rating)
            playerTargetPriceView.setText(Util.getLocaleFormattedStringFromNumber(data.targetPrice))
            when(data.platform){
                Platform.PS -> platformImageView.setImageResource(R.drawable.ic_icons8_playstation)
                Platform.XB -> platformImageView.setImageResource(R.drawable.ic_icons8_xbox)
            }
            if(data.gte){
                gte_lt_toggle.check(R.id.gte_target)
            }
            if(data.lt){
                gte_lt_toggle.check(R.id.lt_target)
            }

            playerTargetPriceView.setValidator(object :
                Validator {
                override var errorMessage: String = ""
                    get() = field

                override fun validate(data: String): Boolean {
                    if(data.isEmpty()){
                        errorMessage = "Tracked Price cannot be empty"
                        return false
                    }
                    if(data.contains(".")){
                        errorMessage = "Tracked Price must be a whole number"
                        return false
                    }
                    if(data.contains("-")){
                        errorMessage = "Tracked Price cannot be negative"
                        return false
                    }
                    return true
                }

            })
            playerTargetPriceView.setPriceListener(object:
                TrackedPriceListener {
                override fun onPriceChanged(newPrice: String) {
                    data.targetPrice = Util.getNumberFromLocaleFormattedString(newPrice)
                }
            })
            Picasso.get().load(data.imageURL).into(playerImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracked_player,parent,false)
        return MyViewHolder(view)
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