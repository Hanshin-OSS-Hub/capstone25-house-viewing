package com.capstone.houseviewingapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capstone.houseviewingapp.databinding.ItemHouseCardBinding

data class HouseCardItem(
    val homeName: String,
    val address: String,
    val ltv : Int
)
    class HouseCardAdapter (private val items: List<HouseCardItem>) : RecyclerView.Adapter<HouseCardAdapter.ViewHolder>() {
    
        inner class ViewHolder(val binding: ItemHouseCardBinding) : RecyclerView.ViewHolder(binding.root)
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemHouseCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }
    
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            // holder.binding.textView.text = item.title
            with(holder.binding){
                homeNameText.text = item.homeName
                addressText.text = item.address
                ltvpercentTextView.text = item.ltv.toString()

                progressBar.max = 100
                progressBar.progress = item.ltv

                val stateColor = when {
                    item.ltv <= 60 -> R.color.blue
                    item.ltv <= 70 -> R.color.amber
                    else -> R.color.red
                }

                val c = ContextCompat.getColor(root.context, stateColor)
                ltvpercentTextView.setTextColor(c)
                progressBar.setIndicatorColor(c)
                percentText.setTextColor(c)

            }
        }
    
        override fun getItemCount(): Int = items.size
    }