package com.hurdle.bluenote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.Question
import com.hurdle.bluenote.databinding.ItemSheetChartListBinding

class SheetChartAdapter : ListAdapter<Question, SheetChartAdapter.SheetChartViewHolder>(SheetQuestionDiffUtil) {

    class SheetChartViewHolder(private val binding: ItemSheetChartListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Question) {
            val numberTextView = binding.sheetChartNumberTextView
            numberTextView.text = item.number.toString()

            if (item.isAnswer) {
                numberTextView.setBackgroundResource(R.drawable.bg_circle)
            } else {
                numberTextView.setBackgroundResource(R.drawable.bg_slash)
            }

            val answerText = findAnswer(item)
            binding.sheetChartAnswerTextView.text = answerText
        }

        private fun findAnswer(item: Question): String {
            var answerText = ""
            if (item.a) {
                answerText += "A"
            }
            if (item.b) {
                answerText += "B "
            }
            if (item.c) {
                answerText += "C"
            }
            if (item.d) {
                answerText += "D"
            }
            if (item.e) {
                answerText += "E"
            }
            return answerText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetChartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSheetChartListBinding.inflate(inflater, parent, false)
        return SheetChartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SheetChartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}