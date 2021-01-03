package com.hurdle.bluenote.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hurdle.bluenote.R
import com.hurdle.bluenote.adapters.NoteAdapter
import com.hurdle.bluenote.adapters.OnNoteClickListener
import com.hurdle.bluenote.databinding.FragmentNoteBinding
import com.hurdle.bluenote.viewmodels.NoteViewModel

class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    private var deleteMenuItem: MenuItem? = null
    private var isDeleteButton = false

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
                }
            } else {
                // 삭제버튼이 선택된 경우
                if (!isLongClick) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(R.string.msg_delete_note)
                        .setPositiveButton(R.string.delete) { _, _ ->
                            // 선택된 노트 db에서 삭제
                            noteViewModel.delete(note)
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

        binding.floatingActionButton.setOnClickListener {
            this.findNavController().navigate(NoteFragmentDirections.actionNavNoteToNavNoteCreate())
        }

        noteViewModel.notes.observe(viewLifecycleOwner) { notes ->
            // 유저가 모든 아이템을 삭제한경우 삭제버튼의 색을 포커스 없을때의 색으로 변경
            if (notes.isEmpty() && deleteMenuItem != null) {
                val menuItem = deleteMenuItem as MenuItem
                menuItem.setIcon(R.drawable.ic_baseline_delete_24)
                isDeleteButton = false
            }

            noteAdapter.submitList(notes)

            // 데이터 추가시 스크롤이 이동이 안되는 현상 방지
            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    // 새로 생성되는 노트는 맨위에 생성
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.forEach { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> menuItem.isVisible = true
                R.id.menu_delete -> menuItem.isVisible = true
            }
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
        Log.d("노트", "onResume: $isDeleteButton")
        isDeleteButton = false
    }
}