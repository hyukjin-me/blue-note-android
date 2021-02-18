package com.hurdle.bluenote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hurdle.bluenote.adapters.*
import com.hurdle.bluenote.databinding.FragmentHomeBinding
import com.hurdle.bluenote.viewmodels.NoteViewModel
import com.hurdle.bluenote.viewmodels.SheetViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sheetAdapter: SheetAdapter

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var sheetViewModel: SheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        sheetViewModel = ViewModelProvider(this).get(SheetViewModel::class.java)

        sheetAdapter = SheetAdapter(true, OnSheetClickListener { sheet, _ ->
            val action = HomeFragmentDirections.actionNavHomeToNavSheetQuestion(
                sheet.id,
                sheet.start,
                sheet.end,
                sheet.title
            )
            this.findNavController().navigate(action)
        })

        noteAdapter = NoteAdapter(true, OnNoteClickListener { note, _, _ ->
            val action =
                HomeFragmentDirections.actionNavHomeToNavNotePage(note.id, note.title)
            this.findNavController().navigate(action)
        })


        binding.homeNoteList.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            setHasFixedSize(true)
        }

        binding.homeSheetList.apply {
            adapter = sheetAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            setHasFixedSize(true)
        }

        noteViewModel.recentNotes.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                noteAdapter.submitList(it)
            }
        }

        sheetViewModel.recentSheets.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                sheetAdapter.submitList(it)
            }
        }
    }
}