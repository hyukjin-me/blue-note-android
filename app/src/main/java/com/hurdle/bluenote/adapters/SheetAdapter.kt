package com.hurdle.bluenote.adapters

import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.databinding.ItemSheetListBinding
import java.text.SimpleDateFormat
import java.util.*


class SheetAdapter(
    private val horizontal: Boolean = false,
    private val clickListener: OnSheetClickListener
) :
    ListAdapter<Sheet, SheetAdapter.SheetViewHolder>(SheetDiffUtil) {

    class SheetViewHolder(private val binding: ItemSheetListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Sheet, clickListener: OnSheetClickListener) {
            // 시트 제목
            val titleTextView = binding.sheetListTitleTextView
            titleTextView.text = item.title

            // 시트의 Question 갯수
            val numberTextView = binding.sheetListNumberTextView
            val str = binding.root.resources.getString(R.string.display_question_range)
            numberTextView.text = String.format(str, item.start, item.end)

            // 시트 생성일
            val dateTextView = binding.sheetListDateTextView
            val timeFormat = SimpleDateFormat("yyyy-MM-dd (E) hh:mm a", Locale.KOREA)
            dateTextView.text = timeFormat.format(item.createTime)

            binding.root.setOnClickListener {
                clickListener.onClick(item, false)
            }

            binding.root.setOnLongClickListener {
                clickListener.onClick(item, true)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSheetListBinding.inflate(inflater, parent, false)

        if (horizontal) {
            val metrics: DisplayMetrics = binding.root.resources.displayMetrics
            val screenHeight = metrics.heightPixels
            val width = screenHeight / 3
            binding.sheetListCardView.layoutParams.width = width
        }

        return SheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SheetViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}

object SheetDiffUtil : DiffUtil.ItemCallback<Sheet>() {
    override fun areItemsTheSame(oldItem: Sheet, newItem: Sheet) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Sheet, newItem: Sheet) = oldItem == newItem
}

class OnSheetClickListener(val clickListener: (sheet: Sheet, isLongClick: Boolean) -> Unit) {
    fun onClick(sheet: Sheet, isLongClick: Boolean) = clickListener(sheet, isLongClick)
}