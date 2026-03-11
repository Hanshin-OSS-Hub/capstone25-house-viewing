package com.capstone.houseviewingapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.ItemAddHouseCardBinding
import com.capstone.houseviewingapp.databinding.ItemHouseCardBinding

class HouseCardAdapter(
    private val items: List<HouseCardItem>,
    private val onDelete: (HouseCardItem, Int) -> Unit,
    // TODO : 수정 기능 구현 시 onEdit: (HouseCardItem, Int) -> Unit 추가 예정
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        private const val VIEW_TYPE_HOUSE = 0
        private const val VIEW_TYPE_ADD = 1
    }

    inner class ViewHolder(val binding: ItemHouseCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class AddViewHolder(val binding: ItemAddHouseCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int =
        if (position == items.size) VIEW_TYPE_ADD else VIEW_TYPE_HOUSE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADD -> {
                val binding = ItemAddHouseCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AddViewHolder(binding)
            }

            else -> {
                val binding = ItemHouseCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {   // [추가] 분기 시작
            is AddViewHolder -> {
                holder.binding.addCardRoot.setOnClickListener { onAddClick() }
            }

            is ViewHolder -> {
                val item = items[position]
                with(holder.binding) {
                    homeNameText.text = item.homeName
                    addressText.text = item.address
                    progressBar.max = 100

                    val ltv = item.ltv
                    if (ltv == null) {
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
                        // TODO: LTV는 나중에 백엔드가 최종 등급을 줌 -> 그걸로 색상 결정하도록 수정 예정
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
                        val wrappedContext = android.view.ContextThemeWrapper(it.context, R.style.Theme_PopupMenuOverlay)
                        val popup = android.widget.PopupMenu(wrappedContext, it)
                        popup.menuInflater.inflate(R.menu.menu_house_card, popup.menu)
                        try {
                            val mPopupField = android.widget.PopupMenu::class.java.getDeclaredField("mPopup")
                            mPopupField.isAccessible = true
                            val menuPopupHelper = mPopupField.get(popup)
                            val getPopupMethod = menuPopupHelper?.javaClass?.getMethod("getPopup")
                            val popupWindow = getPopupMethod?.invoke(menuPopupHelper) as? android.widget.PopupWindow
                            popupWindow?.setBackgroundDrawable(androidx.core.content.ContextCompat.getDrawable(it.context, R.drawable.bg_popup_menu))
                        } catch (_: Exception) { }
                        popup.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.action_delete -> {
                                    onDelete(item, position)
                                    true
                                }
                                else -> false
                            }
                        }
                        popup.show()
                    }
                    homeCard.setOnClickListener { }
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size + 1
}