package com.capstone.houseviewingapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.ItemHouseCardBinding

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

                progressBar.max = 100

                val ltv = item.ltv
                if (ltv == null) {
                    // TODO : LTV 분석 API 연동 후 null(분석 대기) 분기 제거/수정
                    // TODO : else 이후로는 나중에 백엔드와 연동하면 else 이후 부분만 남기고 삭제 -> null이 아닌 실제 LTV 값으로 설정
                    ltvpercentTextView.text = "--"
                    percentText.text = ""
                    progressBar.progress = 0

                    val pendingColor = ContextCompat.getColor(root.context, R.color.icongray)
                    ltvpercentTextView.setTextColor(pendingColor)
                    percentText.setTextColor(pendingColor)
                    progressBar.setIndicatorColor(pendingColor)
                } else {
                    ltvpercentTextView.text = ltv.toString()
                    percentText.text = "%"
                    progressBar.progress = ltv

                    val stateColor = when {
                        ltv <= 60 -> R.color.blue
                        ltv <= 70 -> R.color.amber
                        else -> R.color.red
                    }

                    val c = ContextCompat.getColor(root.context, stateColor)
                    ltvpercentTextView.setTextColor(c)
                    percentText.setTextColor(c)
                    progressBar.setIndicatorColor(c)
                }
                moreButton.setOnClickListener {
                    //TODO : 수정, 삭제, 추가 기능 구현
                }

                homeCard.setOnClickListener {
                    //TODO : 상세 페이지로 이동 기능 구현
                }
            }
        }
    
        override fun getItemCount(): Int = items.size
    }