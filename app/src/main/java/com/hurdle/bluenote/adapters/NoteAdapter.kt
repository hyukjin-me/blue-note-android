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
                clickListener.onClick(item)
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

class OnNoteClickListener(val clickListener: (note: Note) -> Unit) {
    fun onClick(note: Note) = clickListener(note)
}

object NoteDiffUtil : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
}