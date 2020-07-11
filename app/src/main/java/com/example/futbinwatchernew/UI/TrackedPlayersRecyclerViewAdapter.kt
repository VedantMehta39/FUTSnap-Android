package com.example.futbinwatchernew.UI

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.futbinwatchernew.Database.PlayerDBModel
import com.example.futbinwatchernew.Models.Platform
import com.example.futbinwatchernew.R
import com.example.futbinwatchernew.UI.Validators.TextContentValidator
import com.example.futbinwatchernew.Utils.StringFormatter
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso

class TrackedPlayersRecyclerViewAdapter(var data: List<PlayerDBModel>):RecyclerView.Adapter<TrackedPlayersRecyclerViewAdapter.MyViewHolder>(){
    inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val playerNameTextView = view.findViewById<TextView>(R.id.tv_player_name)
        val playerImageView = view.findViewById<ImageView>(R.id.img_player)
        val playerTargetPriceView = view.findViewById<TextInputEditText>(R.id.et_target_price)
        val platformImageView = view.findViewById<ImageView>(R.id.platform_icon)
        val gte_lt_toggle = view.findViewById<MaterialButtonToggleGroup>(R.id.gte_lt_toggle)
        fun bindData(data: PlayerDBModel){
            playerNameTextView.text = (data.name + " " + data.rating)
            playerTargetPriceView.setText(StringFormatter.getLocaleFormattedStringFromNumber(data.targetPrice))
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

            Picasso.get().load(data.imageURL).into(playerImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracked_player,parent,false)
        val holder = MyViewHolder(view)
        holder.playerTargetPriceView.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                text?.let {
                    val validator = TextContentValidator("-.")
                    if(!validator.validate(text.toString())){
                        holder.playerTargetPriceView.error = validator.errorMessage
                    }
                    else{
                        val newTargetPrice = StringFormatter.getNumberFromLocaleFormattedString(text.toString())
                        if((start!=0 || before != 0) &&
                            (data[holder.adapterPosition].targetPrice != newTargetPrice)){


                            data[holder.adapterPosition].targetPrice = newTargetPrice
                            data[holder.adapterPosition].isEdited = true
                        }
                    }

                }
            }

        })
        holder.gte_lt_toggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked){
                when(checkedId){
                    R.id.gte_target -> {
                        if(data[holder.adapterPosition].gte != true){
                            data[holder.adapterPosition].isEdited = true
                            data[holder.adapterPosition].gte = true
                            data[holder.adapterPosition].lt = false
                        }
                    }
                    R.id.lt_target -> {
                        if(data[holder.adapterPosition].lt != true){
                            data[holder.adapterPosition].isEdited = true
                            data[holder.adapterPosition].lt = true
                            data[holder.adapterPosition].gte = false
                        }

                    }

                }
            }
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