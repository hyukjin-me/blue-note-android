package com.hurdle.bluenote.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hurdle.bluenote.adapters.NoteAdapter
import com.hurdle.bluenote.adapters.OnNoteClickListener
import com.hurdle.bluenote.databinding.FragmentNoteBinding
import com.hurdle.bluenote.viewmodels.NoteViewModel

class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        noteAdapter = NoteAdapter(OnNoteClickListener { note, isLongClick ->
            if (isLongClick) {
                // 편집화면으로 이동
                noteViewModel.navigateToEdit(note)
            } else {
                // 상세화면으로 이동
            }
        })

        binding.noteList.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.floatingActionButton.setOnClickListener {
            this.findNavController().navigate(NoteFragmentDirections.actionNavNoteToNavNoteCreate())
        }

        noteViewModel.notes.observe(viewLifecycleOwner) { notes ->
            noteAdapter.submitList(notes)

            // 데이터 추가시 스크롤이 이동이 안되는 현상 방지
            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    binding.noteList.scrollToPosition(0)
                }
            }, 200)
        }

        noteViewModel.navigateToEdit.observe(viewLifecycleOwner) { note ->
            if (note != null) {
                val action = NoteFragmentDirections.actionNavNoteToNavNoteCreate(note.id)
                this.findNavController().navigate(action)

                noteViewModel.doneNavigateEdit()
            }
        }
    }
}