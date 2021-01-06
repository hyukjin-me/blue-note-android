package com.hurdle.bluenote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hurdle.bluenote.data.NotePage
import com.hurdle.bluenote.databinding.ItemNotePageListBinding
import com.hurdle.bluenote.fragments.NotePageFragment.Companion.checkDay
import java.text.SimpleDateFormat
import java.util.*

class NotePageAdapter(private val notePageClickListener: OnNotePageClickListener) :
    ListAdapter<NotePage, NotePageAdapter.NotePageViewHolder>(NotePageDiffUtil) {

    class NotePageViewHolder(private val binding: ItemNotePageListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotePage, clickListener: OnNotePageClickListener) {

            // 시간 차이를 계산하기위해 long type 을 Date 타입으로 변환
            val date = Date(item.time)

            val displayDayFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale.getDefault())
            val displayDayText = displayDayFormat.format(date)

            if (checkDay != displayDayText) {
                binding.pageListDayTextView.visibility = View.VISIBLE
                binding.pageListDayTextView.text = displayDayText
                checkDay = displayDayText
            } else {
                binding.pageListDayTextView.visibility = View.GONE
            }

            // 화면에 시간표시
            val hourMinuteTextView = binding.pageListClockTextView
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            hourMinuteTextView.text = timeFormat.format(date)

            // 펼쳐보기
            val wordContentText = item.content
            val lines: List<String> = wordContentText.lines()
            val count = wordContentText.count()
            val size = lines.size

            // 라인 및 글자수 제한
            if (size >= 6 || count > 200) {
                binding.pageListExpansionTextView.visibility = View.VISIBLE
            }

            // 내용
            binding.pageListContentsTextView.text = wordContentText

            // 클릭 이벤트
            binding.root.setOnClickListener {
                clickListener.onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotePageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotePageListBinding.inflate(inflater, parent, false)
        return NotePageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotePageViewHolder, position: Int) {
        holder.bind(getItem(position), notePageClickListener)
    }
}

class OnNotePageClickListener(val clickListener: (notePage: NotePage) -> Unit) {
    fun onClick(notePage: NotePage) = clickListener(notePage)
}

object NotePageDiffUtil : DiffUtil.ItemCallback<NotePage>() {
    override fun areItemsTheSame(oldItem: NotePage, newItem: NotePage) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: NotePage, newItem: NotePage) = oldItem == newItem
}