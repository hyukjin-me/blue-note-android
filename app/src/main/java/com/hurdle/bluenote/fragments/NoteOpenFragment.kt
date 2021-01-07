package com.hurdle.bluenote.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hurdle.bluenote.MainActivity
import com.hurdle.bluenote.R
import com.hurdle.bluenote.databinding.FragmentNoteOpenBinding
import com.hurdle.bluenote.viewmodels.NotePageViewModel
import com.hurdle.bluenote.viewmodels.NotePageViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class NoteOpenFragment : Fragment() {

    private lateinit var binding: FragmentNoteOpenBinding
    private lateinit var notePageViewModel: NotePageViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = activity as MainActivity
        activity.setToolbarTitle("")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteOpenBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getData = NoteOpenFragmentArgs.fromBundle(requireArguments())
        val id = getData.id
        val noteId = getData.noteId

        val application = requireActivity().application
        val pageFactory = NotePageViewModelFactory(application = application, noteId)
        notePageViewModel = ViewModelProvider(this, pageFactory).get(NotePageViewModel::class.java)

        notePageViewModel.getNotePageItem(id)

        notePageViewModel.pageItem.observe(viewLifecycleOwner) { notePageItem ->
            if (notePageItem != null) {

                // 툴바 타이틀 변경
                val dateFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale.getDefault())
                val displayOfTimeText = dateFormat.format(notePageItem.time).toString()
                val activity = activity as MainActivity
                activity.setToolbarTitle(displayOfTimeText)

                binding.noteOpenTitleTextView.text = notePageItem.content
            }
        }

        binding.noteOpenCloseButton.setOnClickListener {
            this.findNavController().popBackStack()
        }
    }
}