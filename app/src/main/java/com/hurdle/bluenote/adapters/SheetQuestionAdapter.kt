package com.hurdle.bluenote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.Question
import com.hurdle.bluenote.databinding.ItemSheetQuestionListBinding
import com.hurdle.bluenote.utils.setButtonTextColor

class SheetQuestionAdapter(private val clickListener: OnQuestionClickListener) :
    ListAdapter<Question, SheetQuestionAdapter.SheetQuestionViewHolder>(SheetQuestionDiffUtil) {

    class SheetQuestionViewHolder(private val binding: ItemSheetQuestionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Question, clickListener: OnQuestionClickListener) {

            val numberTextView = binding.questionListNumberTextView
            // Question 번호 표시
            numberTextView.text = item.number.toString()

            //  정답 여부에 따라 번호에 O,X 화면에 표시, (기본 설정 false)
            if (item.isAnswer) {
                numberTextView.setBackgroundResource(R.drawable.bg_circle)
            } else {
                numberTextView.setBackgroundResource(R.drawable.bg_slash)
            }

            // 번호 클릭시 O, X 변경
            numberTextView.setOnClickListener {
                item.isAnswer = !item.isAnswer
                clickListener.onClick(item)
            }

            val btnA = binding.questionListAButton
            val btnB = binding.questionListBButton
            val btnC = binding.questionListCButton
            val btnD = binding.questionListDButton
            val btnE = binding.questionListEButton
            val btns = listOf(btnA, btnB, btnC, btnD, btnE)

            val context = binding.root.context
            setButtonTextColor(context, btnA, item.a)
            setButtonTextColor(context, btnB, item.b)
            setButtonTextColor(context, btnC, item.c)
            setButtonTextColor(context, btnD, item.d)
            setButtonTextColor(context, btnE, item.e)

            btns.forEach { btn ->
                btn.setOnClickListener {
                    when (btn.text) {
                        "A" -> {
                            item.a = !item.a
                            setButtonTextColor(context, btn, item.a)
                        }
                        "B" -> {
                            item.b = !item.b
                            setButtonTextColor(context, btn, item.b)
                        }
                        "C" -> {
                            item.c = !item.c
                            setButtonTextColor(context, btn, item.c)
                        }
                        "D" -> {
                            item.d = !item.d
                            setButtonTextColor(context, btn, item.d)
                        }
                        "E" -> {
                            item.e = !item.e
                            setButtonTextColor(context, btn, item.e)
                        }
                    }

                    btn.isSelected = !btn.isSelected
                    clickListener.onClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetQuestionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSheetQuestionListBinding.inflate(inflater, parent, false)
        return SheetQuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SheetQuestionViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}

class OnQuestionClickListener(val clickListener: (question: Question) -> Unit) {
    fun onClick(question: Question) = clickListener(question)
}

object SheetQuestionDiffUtil : DiffUtil.ItemCallback<Question>() {
    override fun areItemsTheSame(oldItem: Question, newItem: Question) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Question, newItem: Question) = oldItem == newItem
}