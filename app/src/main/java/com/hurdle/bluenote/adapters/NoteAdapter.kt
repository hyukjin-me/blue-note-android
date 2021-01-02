package com.hurdle.bluenote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.Note
import com.hurdle.bluenote.databinding.ItemNoteListBinding

class NoteAdapter(private val clickListener: OnNoteClickListener) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffUtil) {

    class NoteViewHolder(private val binding: ItemNoteListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Note, clickListener: OnNoteClickListener) {

            // setTitle
            val titleFormat = binding.root.resources.getString(R.string.display_title)
            binding.noteListTitleTextView.text = String.format(titleFormat, item.title)

            binding.root.setOnClickListener {
                clickListener.onClick(item, false)
            }

            binding.root.setOnLongClickListener {
                clickListener.onClick(item, true)
                // 롱클릭만 처리하기위해 true 설정
                // false 설정시 클릭리스너도 같이 처리됨
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNoteListBinding.inflate(inflater, parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}

class OnNoteClickListener(val clickListener: (note: Note, isLongClick: Boolean) -> Unit) {
    fun onClick(note: Note, isLongClick: Boolean) = clickListener(note, isLongClick)
}

object NoteDiffUtil : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
}