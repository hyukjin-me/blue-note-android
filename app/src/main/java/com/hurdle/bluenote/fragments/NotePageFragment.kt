package com.hurdle.bluenote.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hurdle.bluenote.MainActivity
import com.hurdle.bluenote.databinding.FragmentNotePageBinding

class NotePageFragment : Fragment() {

    private lateinit var binding: FragmentNotePageBinding

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
    }
}