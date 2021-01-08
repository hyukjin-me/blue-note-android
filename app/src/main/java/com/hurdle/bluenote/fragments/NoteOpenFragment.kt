package com.hurdle.bluenote.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hurdle.bluenote.MainActivity
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.NotePage
import com.hurdle.bluenote.databinding.FragmentNoteOpenBinding
import com.hurdle.bluenote.viewmodels.NotePageViewModel
import com.hurdle.bluenote.viewmodels.NotePageViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class NoteOpenFragment : Fragment() {

    private lateinit var binding: FragmentNoteOpenBinding
    private lateinit var notePageViewModel: NotePageViewModel

    private var id: Long = -1L
    private var noteId: Long = -1L

    private var isEdit = false

    private lateinit var notePage: NotePage

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

        id = getData.id
        noteId = getData.noteId

        val application = requireActivity().application
        val pageFactory = NotePageViewModelFactory(application = application, noteId)
        notePageViewModel = ViewModelProvider(this, pageFactory).get(NotePageViewModel::class.java)

        notePageViewModel.getNotePageItem(id)
        notePageViewModel.pageItem.observe(viewLifecycleOwner) { notePageItem ->
            if (notePageItem != null) {
                notePage = notePageItem
                // 툴바 타이틀 변경
                val dateFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale.getDefault())
                val displayOfTimeText = dateFormat.format(notePageItem.time).toString()
                val activity = activity as MainActivity
                activity.setToolbarTitle(displayOfTimeText)

                binding.noteOpenTitleTextView.setText(notePageItem.content)
            }
        }

        binding.noteOpenCloseButton.setOnClickListener {
            this.findNavController().popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.findItem(R.id.menu_edit).isVisible = true
        menu.findItem(R.id.menu_delete).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deleteDialog()
            }
            R.id.menu_edit -> {
                isEdit = !isEdit
                if (isEdit) {
                    item.setIcon(R.drawable.ic_baseline_save_24)
                    binding.noteOpenLinearLayout.setBackgroundResource(R.drawable.bg_square)
                    binding.noteOpenTitleTextView.isEnabled = true
                } else {
                    saveDialog(item)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveDialog(item: MenuItem) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.msg_save_note)
            .setPositiveButton(R.string.save) { _, _ ->
                // Edit 아이콘 변경
                item.setIcon(R.drawable.ic_baseline_edit_24)
                // 테투리 배경 제거
                binding.noteOpenLinearLayout.setBackgroundResource(0)
                binding.noteOpenTitleTextView.isEnabled = false

                val inputText = binding.noteOpenTitleTextView.text.toString()
                notePage.content = inputText

                notePageViewModel.update(notePage)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                isEdit = true
            }
            .create()
        builder.show()
    }

    private fun deleteDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.msg_delete_note)
            .setPositiveButton(R.string.delete) { _, _ ->
                notePageViewModel.deleteNotePageItem(id, noteId)
                this.findNavController().popBackStack()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
            }
            .create()
        builder.show()
    }
}