package com.hurdle.bluenote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.Home
import com.hurdle.bluenote.databinding.ItemHomeListBinding
import com.hurdle.bluenote.utils.NOTE
import com.hurdle.bluenote.utils.SHEET

class HomeAdapter : ListAdapter<Home, HomeAdapter.HomeViewHolder>(HomeDiffUtil) {

    class HomeViewHolder(private val binding: ItemHomeListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Home) {

            if (item.idText == NOTE) {
                binding.homeItemTextView.text =
                    binding.root.resources.getString(R.string.recent_note)

                val notes = item.notes

                val noteAdapter = NoteAdapter(horizontal = true, OnNoteClickListener { note, _, _ ->

                })

                binding.homeListList.apply {
                    adapter = noteAdapter
                    layoutManager = LinearLayoutManager(
                        binding.root.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    setHasFixedSize(true)
                }

                noteAdapter.submitList(notes)
            }

            if (item.idText == SHEET) {
                binding.homeItemTextView.text =
                    binding.root.resources.getString(R.string.recent_sheet)

                val sheets = item.sheets

                val sheetAdapter = SheetAdapter(horizontal = true, OnSheetClickListener { sheet, _ ->

                })

                binding.homeListList.apply {
                    adapter = sheetAdapter
                    layoutManager = LinearLayoutManager(
                        binding.root.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    setHasFixedSize(true)
                }

                sheetAdapter.submitList(sheets)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeListBinding.inflate(inflater, parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object HomeDiffUtil : DiffUtil.ItemCallback<Home>() {
    override fun areItemsTheSame(oldItem: Home, newItem: Home): Boolean {
        return oldItem.idText == newItem.idText
    }

    override fun areContentsTheSame(oldItem: Home, newItem: Home): Boolean {
        return oldItem == newItem
    }
}