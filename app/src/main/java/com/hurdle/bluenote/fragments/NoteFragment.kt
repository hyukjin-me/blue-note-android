package com.hurdle.bluenote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hurdle.bluenote.databinding.FragmentHomeBinding
import com.hurdle.bluenote.databinding.FragmentNoteBinding
import com.hurdle.bluenote.viewmodels.NoteViewModel

class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
    }
}