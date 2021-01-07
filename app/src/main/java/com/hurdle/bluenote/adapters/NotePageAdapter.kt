package com.hurdle.bluenote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.NotePage
import com.hurdle.bluenote.databinding.ItemNotePageListBinding
import com.hurdle.bluenote.fragments.NotePageFragment.Companion.checkDay
import java.text.SimpleDateFormat
import java.util.*

class NotePageAdapter(private val notePageClickListener: OnNotePageClickListener) :
    ListAdapter<NotePage, NotePageAdapter.NotePageViewHolder>(NotePageDiffUtil) {

    private var isEdit = false

    class NotePageViewHolder(private val binding: ItemNotePageListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotePage, clickListener: OnNotePageClickListener, isEdit: Boolean) {

            // 시간 차이를 계산하기위해 long type 을 Date 타입으로 변환
            val date = Date(item.time)

            // 화면에 시간표시
            val hourMinuteTextView = binding.pageListClockTextView
            val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            hourMinuteTextView.text = timeFormat.format(date)

            // 펼쳐보기
            val wordContentText = item.content
            val lines: List<String> = wordContentText.lines()
            val count = wordContentText.count()
            val size = lines.size

            if (!isEdit) {
                // 라인 및 글자수 초과시 OPEN 버튼 보이게 처리
                if (size >= 6 || count > 200) {
                    binding.pageListExpansionTextView.visibility = View.VISIBLE

                    val openText = binding.root.resources.getString(R.string.open)
                    binding.pageListExpansionTextView.text = String.format(openText)
                } else {
                    binding.pageListExpansionTextView.visibility = View.INVISIBLE
                }
            } else {
                // Edit 모드
                binding.pageListExpansionTextView.visibility = View.VISIBLE

                val editText = binding.root.resources.getString(R.string.edit)
                binding.pageListExpansionTextView.text = String.format(editText)
            }

            // 내용
            binding.pageListContentsTextView.text = wordContentText

            binding.pageListExpansionTextView.setOnClickListener {
                clickListener.onClick(item, "open")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotePageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotePageListBinding.inflate(inflater, parent, false)
        return NotePageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotePageViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, notePageClickListener, isEdit)
    }

    fun setEditMode(isEdit: Boolean) {
        this.isEdit = isEdit
        checkDay = ""
        notifyDataSetChanged()
    }
}

class OnNotePageClickListener(val clickListener: (notePage: NotePage, message: String) -> Unit) {

    fun onClick(notePage: NotePage, message: String) = clickListener(notePage, message)
}

object NotePageDiffUtil : DiffUtil.ItemCallback<NotePage>() {
    override fun areItemsTheSame(oldItem: NotePage, newItem: NotePage) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: NotePage, newItem: NotePage) = oldItem == newItem
}