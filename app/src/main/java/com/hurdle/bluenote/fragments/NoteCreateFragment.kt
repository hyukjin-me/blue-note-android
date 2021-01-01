package com.hurdle.bluenote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hurdle.bluenote.databinding.FragmentNoteCreateBinding
import com.hurdle.bluenote.viewmodels.NoteCreateViewModel

class NoteCreateFragment : Fragment() {

    private lateinit var binding: FragmentNoteCreateBinding

    private lateinit var noteCreateViewModel: NoteCreateViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteCreateBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteCreateViewModel = ViewModelProvider(this).get(NoteCreateViewModel::class.java)
    }
}