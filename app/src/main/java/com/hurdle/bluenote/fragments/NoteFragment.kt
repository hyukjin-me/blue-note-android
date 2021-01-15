package com.hurdle.bluenote.fragments

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hurdle.bluenote.R
import com.hurdle.bluenote.adapters.NoteAdapter
import com.hurdle.bluenote.adapters.OnNoteClickListener
import com.hurdle.bluenote.data.Note
import com.hurdle.bluenote.databinding.FragmentNoteBinding
import com.hurdle.bluenote.utils.MAX_SEARCH_LENGTH
import com.hurdle.bluenote.viewmodels.NoteViewModel

class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var searchView: SearchView
    private lateinit var searchViewEditText: EditText

    private var deleteMenuItem: MenuItem? = null
    private var isDeleteButton = false

    private var notes = emptyList<Note>()

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

        noteAdapter = NoteAdapter(OnNoteClickListener { note, isLongClick, position ->
            if (!isDeleteButton) {
                if (isLongClick) {
                    // 편집화면으로 이동
                    noteViewModel.navigateToEdit(note)
                } else {
                    // 상세화면으로 이동
                    noteViewModel.navigateToPage(note)
                }
            } else {
                // 삭제버튼이 선택된 경우
                if (!isLongClick) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(R.string.msg_delete_note)
                        .setPositiveButton(R.string.delete) { _, _ ->
                            // 선택된 노트 db에서 삭제
                            noteViewModel.delete(note)
                            noteViewModel.deleteNotePages(note.id)
                        }
                        .setNegativeButton(R.string.cancel) { _, _ ->
                        }
                        .create()
                    builder.show()
                }
            }
        })

        binding.noteList.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.noteAddFab.setOnClickListener {
            this.findNavController().navigate(NoteFragmentDirections.actionNavNoteToNavNoteCreate())
        }

        noteViewModel.notes.observe(viewLifecycleOwner) { notes ->
            // 유저가 모든 아이템을 삭제한경우 삭제버튼의 색을 포커스 없을때의 색으로 변경
            if (notes.isEmpty() && deleteMenuItem != null) {
                val menuItem = deleteMenuItem as MenuItem
                menuItem.setIcon(R.drawable.ic_baseline_delete_24)
                isDeleteButton = false
            }
            this.notes = notes
            noteAdapter.submitList(notes)

            // 데이터 추가시 스크롤이 이동이 안되는 현상 방지
            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    // 새로 생성되는 노트는 맨위에 생성
                    binding.noteList.scrollToPosition(0)
                }
            }, 200)
        }

        // 편집화면으로 이동
        noteViewModel.navigateToEdit.observe(viewLifecycleOwner) { note ->
            if (note != null) {
                val action = NoteFragmentDirections.actionNavNoteToNavNoteCreate(note.id)
                this.findNavController().navigate(action)

                noteViewModel.doneNavigateEdit()
            }
        }

        // 상제화면(페이지)화면으로 이동
        noteViewModel.navigateToPage.observe(viewLifecycleOwner) { note ->
            if (note != null) {
                val action =
                    NoteFragmentDirections.actionNavNoteToNavNotePage(note.id, note.title)
                this.findNavController().navigate(action)

                noteViewModel.doneNavigatePage()
            }
        }

        noteViewModel.searchNote.observe(viewLifecycleOwner) {
            noteAdapter.submitList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val editMenu = menu.findItem(R.id.menu_edit)
        editMenu.isVisible = true

        val deleteMenu = menu.findItem(R.id.menu_delete)
        deleteMenu.isVisible = true

        menu.findItem(R.id.menu_search).apply {
            isVisible = true
            searchView = this.actionView as SearchView
        }

        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

        // searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            setIconifiedByDefault(true)
            searchViewEditText =
                this.findViewById(androidx.appcompat.R.id.search_src_text)

            // 글자수 제한
            searchViewEditText.filters = arrayOf(InputFilter.LengthFilter(MAX_SEARCH_LENGTH))

            this.setOnQueryTextFocusChangeListener { _, hasFocus ->
                when (hasFocus) {
                    true -> {
                        editMenu.isVisible = false
                        deleteMenu.isVisible = false
                    }
                    false -> {
                        editMenu.isVisible = true
                        deleteMenu.isVisible = true

                        noteAdapter.submitList(notes)
                    }
                }
            }

            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrEmpty()) {
                        noteViewModel.searchNote("%$query%")
                    }
                    hideKeyboard(binding.root)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val inputText = newText ?: ""
                    if (inputText.count() >= 12) {
                        Snackbar.make(
                            binding.root,
                            context.getString(R.string.search_max_count),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    return true
                }
            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.msg_edit_note),
                    Snackbar.LENGTH_LONG
                ).show()
                return true
            }
            R.id.menu_delete -> {
                if (!isDeleteButton) {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.msg_delete_note_guide),
                        Snackbar.LENGTH_LONG
                    ).show()
                    item.setIcon(R.drawable.ic_baseline_delete_24_red)
                    deleteMenuItem = item
                } else {
                    item.setIcon(R.drawable.ic_baseline_delete_24)
                }

                isDeleteButton = !isDeleteButton
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        isDeleteButton = false
        noteAdapter.submitList(notes)
    }

    private fun hideKeyboard(it: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}