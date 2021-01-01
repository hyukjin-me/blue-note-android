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
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.Note
import com.hurdle.bluenote.databinding.FragmentNoteCreateBinding
import com.hurdle.bluenote.viewmodels.NoteCreateViewModel
import com.hurdle.bluenote.viewmodels.NoteViewModel

class NoteCreateFragment : Fragment() {

    private lateinit var binding: FragmentNoteCreateBinding

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteCreateViewModel: NoteCreateViewModel

    private lateinit var inputText: EditText
    private lateinit var countText: TextView

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
        noteCreateViewModel = ViewModelProvider(this).get(NoteCreateViewModel::class.java)

        inputText = binding.noteCreateTitleEditText
        countText = binding.noteCreateCountTextView

        val count: Int = inputText.text.toString().count()
        setCountText(count)

        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                setCountText(s?.count() ?: 0)
            }
        })

        binding.noteCreateSaveButton.setOnClickListener {
            val note = Note(title = inputText.text.toString())
            noteViewModel.insert(note)
            this.findNavController().popBackStack()
            hideKeyboard(it)
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
}