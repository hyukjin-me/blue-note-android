package com.hurdle.bluenote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.Question
import com.hurdle.bluenote.databinding.ItemSheetQuestionListBinding
import com.hurdle.bluenote.utils.setButtonTextColor

class SheetHelperAdapter(private val clickListener: OnQuestionClickListener) :
    ListAdapter<Question, SheetHelperAdapter.HelperViewHolder>(SheetQuestionDiffUtil) {

    class HelperViewHolder(private val binding: ItemSheetQuestionListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Question,
            clickListener: OnQuestionClickListener
        ) {
            val numberTextView = binding.questionListNumberTextView
            numberTextView.text = item.number.toString()

            if (item.isAnswer) {
                numberTextView.setBackgroundResource(R.drawable.bg_circle)
            } else {
                numberTextView.setBackgroundResource(R.drawable.bg_slash)
            }

            numberTextView.setOnClickListener {
                item.isAnswer = !item.isAnswer
                clickListener.onClick(item)
            }

            val btnA = binding.questionListAButton
            val btnB = binding.questionListBButton
            val btnC = binding.questionListCButton
            val btnD = binding.questionListDButton
            val btnE = binding.questionListEButton

            val btn = listOf(btnA, btnB, btnC, btnD, btnE)

            val context = binding.root.context
            setButtonTextColor(context, btnA, item.a)
            setButtonTextColor(context, btnB, item.b)
            setButtonTextColor(context, btnC, item.c)
            setButtonTextColor(context, btnD, item.d)
            setButtonTextColor(context, btnE, item.e)

            btn.forEach { button ->
                button.setOnClickListener {
                    when (button.text) {
                        "A" -> {
                            item.a = !item.a
                            setButtonTextColor(context,button, item.a)
                        }
                        "B" -> {
                            item.b = !item.b
                            setButtonTextColor(context,button, item.b)
                        }
                        "C" -> {
                            item.c = !item.c
                            setButtonTextColor(context,button, item.c)
                        }
                        "D" -> {
                            item.d = !item.d
                            setButtonTextColor(context,button, item.d)
                        }
                        "E" -> {
                            item.e = !item.e
                            setButtonTextColor(context,button, item.e)
                        }
                    }

                    clickListener.onClick(item)
                }
            }

            val playText = binding.root.resources.getString(R.string.display_time)
            binding.questionListTimeTextView.text = String.format(playText, item.time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSheetQuestionListBinding.inflate(inflater, parent, false)
        return HelperViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HelperViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}