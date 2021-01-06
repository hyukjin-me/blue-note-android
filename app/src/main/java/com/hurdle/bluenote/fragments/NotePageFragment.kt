package com.hurdle.bluenote.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hurdle.bluenote.MainActivity
import com.hurdle.bluenote.databinding.FragmentNotePageBinding
import java.text.SimpleDateFormat
import java.util.*

class NotePageFragment : Fragment() {

    private lateinit var binding: FragmentNotePageBinding

    val timeHandler = Handler(Looper.getMainLooper())

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val title = NotePageFragmentArgs.fromBundle(requireArguments()).title
        val activity = activity as MainActivity
        activity.setToolbarTitle(title)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotePageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayOfTime()
    }

    private fun displayOfTime() {
        // 실시간 시간표시
        // https://stackoverflow.com/questions/55570990/kotlin-call-a-function-every-second
        timeHandler.post(object : Runnable {
            override fun run() {
                val calendar = Calendar.getInstance()
                val fullTimeFormat = SimpleDateFormat("yyyy-MM-dd (E) aa HH:mm:ss", Locale.getDefault())
                val displayOfDate = fullTimeFormat.format(calendar.time)
                binding.noteWordTimeTextView.text = displayOfDate

                timeHandler.postDelayed(this, 1000)
            }
        })
    }
}