package com.capstone.houseviewingapp.registration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.houseviewingapp.databinding.ItemAttachedFileBinding

class AttachedFileAdapter(
    private val items: MutableList<AttachedFileUiModel>,
    private val onDeleteClick: (AttachedFileUiModel) -> Unit // 어떤 파일이 삭제버튼을 클릭했는지 알려주는 콜백 함수
) : RecyclerView.Adapter<AttachedFileAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAttachedFileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAttachedFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.fileNameTextView.text = item.fileName
        holder.binding.fileDescTextView.text = item.fileMeta
        holder.binding.deleteIcon.setOnClickListener {
            onDeleteClick(item)
        }
    }

    //NOTE : 아이템 개수를 반환하는 함수, items 리스트의 크기를 반환
    override fun getItemCount(): Int = items.size

    //NOTE : 새로운 리스트로 업데이트하는 함수, 기존 리스트를 clear하고 새 리스트로 채운 후 notifyDataSetChanged() 호출
    fun submitList(newItems: List<AttachedFileUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}