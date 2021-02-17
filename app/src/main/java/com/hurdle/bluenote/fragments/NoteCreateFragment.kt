package com.hurdle.bluenote.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hurdle.bluenote.MainActivity
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.Note
import com.hurdle.bluenote.databinding.FragmentNoteCreateBinding
import com.hurdle.bluenote.viewmodels.NoteViewModel

class NoteCreateFragment : Fragment() {

    private lateinit var binding: FragmentNoteCreateBinding

    private lateinit var noteViewModel: NoteViewModel

    private lateinit var inputText: EditText
    private lateinit var countText: TextView
    private lateinit var note: Note

    private var noteId = -1L

    override fun onAttach(context: Context) {
        super.onAttach(context)

        noteId = NoteCreateFragmentArgs.fromBundle(requireArguments()).id
        if (noteId != -1L) {
            val activity = activity as MainActivity
            activity.setToolbarTitle(getString(R.string.edit_mode))
        }
    }

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

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        val saveButton = binding.noteCreateSaveButton
        inputText = binding.noteCreateTitleEditText
        countText = binding.noteCreateCountTextView

        val count: Int = inputText.text.toString().count()
        setCountText(count)

        // 편집모드
        if (noteId != -1L) {
            // Note 데이터 조회
            noteViewModel.getNote(noteId)
        }

        noteViewModel.note.observe(viewLifecycleOwner) { note ->
            if (note != null) {
                this.note = note

                inputText.setText(note.title)
                binding.noteCreateSaveButton.text = getString(R.string.edit)
            }
        }

        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                setCountText(s?.count() ?: 0)
            }
        })

        saveButton.setOnClickListener {
            val title = inputText.text.toString()

            if (saveButton.text == getString(R.string.save)) {
                // 생성모드
                if (checkInput(title)) {
                    noteViewModel.insert(Note(title = title))

                    this.findNavController().popBackStack()
                    hideKeyboard(it)
                }
            } else if (saveButton.text == getString(R.string.edit)) {
                // 편집모드
                if (checkInput(title)) {
                    note.title = title
                    noteViewModel.update(note)

                    this.findNavController().popBackStack()
                    hideKeyboard(it)
                }
            }
        }

        binding.noteCreateCancelButton.setOnClickListener {
            this.findNavController().popBackStack()
        }
    }

    // 유저가 입력한 텍스트의 갯수를 화면에 보여줌
    private fun setCountText(count: Int) {
        countText.text = String.format(getString(R.string.display_count), count)
    }

    private fun hideKeyboard(it: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    private fun checkInput(title: String): Boolean {
        // 빈텍스트 허용안함
        if (title.isEmpty()) {
            hideKeyboard(requireView())
            Snackbar.make(requireView(), "Error, empty text", Snackbar.LENGTH_LONG).show()
            return false
        }

        // 글자 100자 초과 허용안함
        if (title.length > 100) {
            hideKeyboard(requireView())
            Snackbar.make(
                requireView(),
                "Exceeding the maximum number of character",
                Snackbar.LENGTH_LONG
            ).show()
            return false
        }

        return true
    }
}