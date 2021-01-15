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
import com.hurdle.bluenote.adapters.OnSheetClickListener
import com.hurdle.bluenote.adapters.SheetAdapter
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.databinding.FragmentSheetBinding
import com.hurdle.bluenote.utils.MAX_SEARCH_LENGTH
import com.hurdle.bluenote.viewmodels.SheetViewModel

class SheetFragment : Fragment() {

    private lateinit var binding: FragmentSheetBinding

    private lateinit var sheetViewModel: SheetViewModel
    private lateinit var sheetAdapter: SheetAdapter

    private lateinit var searchView: SearchView
    private lateinit var searchViewEditText: EditText

    private var sheets: List<Sheet> = emptyList()

    private var deleteMenuItem: MenuItem? = null
    private var isDeleteButton = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSheetBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sheetViewModel = ViewModelProvider(this).get(SheetViewModel::class.java)

        sheetAdapter = SheetAdapter(OnSheetClickListener { sheet, isLongClick ->
            if (!isDeleteButton) {
                if (isLongClick) {
                    // 편집화면으로 이동
                    sheetViewModel.navigateToEdit(sheet)
                } else {
                    // Question 화면으로 이동
                    sheetViewModel.navigateToQuestion(sheet)
                }
            } else {
                // 삭제버튼이 선택된 경우
                if (!isLongClick) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(R.string.msg_delete_note)
                        .setPositiveButton(R.string.delete) { _, _ ->
                            // 선택된 노트 db에서 삭제
                            sheetViewModel.deleteSheet(sheet)
                            sheetViewModel.deleteSheetQuestions(sheet.id)
                        }
                        .setNegativeButton(R.string.cancel) { _, _ ->
                        }
                        .create()
                    builder.show()
                }
            }
        })

        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.sheetList.apply {
            adapter = sheetAdapter
            layoutManager = linearLayoutManager
        }

        sheetViewModel.sheets.observe(viewLifecycleOwner) { sheets ->
            this.sheets = sheets
            sheetAdapter.submitList(sheets)

            // https://dreamaz.tistory.com/249
            // 안드로이드 RecyclerView scrollToPosition이 제대로 동작하지 않을 때
            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    binding.sheetList.scrollToPosition(0)
                }
            }, 200)
        }

        sheetViewModel.searchSheet.observe(viewLifecycleOwner) {
            sheetAdapter.submitList(it)
        }

        sheetViewModel.navigateToEdit.observe(viewLifecycleOwner) {
            if (it != null) {
                val action = SheetFragmentDirections.actionNavSheetToNavSheetCreate(it.id)
                this.findNavController().navigate(action)

                sheetViewModel.doneNavigateEdit()
            }
        }

        sheetViewModel.navigateToQuestion.observe(viewLifecycleOwner) {
            if (it != null) {
                val action = SheetFragmentDirections.actionNavSheetToNavSheetQuestion()
                this.findNavController().navigate(action)

                sheetViewModel.doneNavigateQuestion()
            }
        }

        binding.sheetAddFab.setOnClickListener {
            // 시트생성화면으로 이동
            navigateCreatePage()
        }
    }

    override fun onResume() {
        super.onResume()

        sheetAdapter.submitList(sheets)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val editMenu = menu.findItem(R.id.menu_edit)
        editMenu.isVisible = true

        val deleteMenu = menu.findItem(R.id.menu_delete)
        deleteMenu.isVisible = true

        val searchMenu = menu.findItem(R.id.menu_search)
        searchMenu.isVisible = true
        searchView = searchMenu.actionView as SearchView
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
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
                    }
                    false -> {
                        // val newSheet = sheets.map { it }

                        sheetAdapter.submitList(sheets)
                    }
                }
            }

            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrEmpty()) {
                        sheetViewModel.searchSheet("%$query%")
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

    private fun navigateCreatePage() {
        this.findNavController().navigate(SheetFragmentDirections.actionNavSheetToNavSheetCreate())
    }

    private fun hideKeyboard(it: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}