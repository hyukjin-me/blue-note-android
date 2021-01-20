package com.hurdle.bluenote.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hurdle.bluenote.MainActivity
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.databinding.FragmentSheetCreateBinding
import com.hurdle.bluenote.utils.MAX_QUESTION
import com.hurdle.bluenote.viewmodels.SheetViewModel

class SheetCreateFragment : Fragment() {

    private lateinit var binding: FragmentSheetCreateBinding
    private lateinit var sheetViewModel: SheetViewModel

    private var sheetId = -1L
    private var sheet: Sheet? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sheetId = SheetCreateFragmentArgs.fromBundle(requireArguments()).id
        if (sheetId != -1L) {
            val activity = activity as MainActivity
            activity.setToolbarTitle(getString(R.string.edit_mode))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSheetCreateBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sheetViewModel = ViewModelProvider(this).get(SheetViewModel::class.java)

        // 편집모드
        if (sheetId != -1L) {
            // Sheet 데이터 조회
            sheetViewModel.getSheet(sheetId)
        }

        sheetViewModel.sheet.observe(viewLifecycleOwner) {
            if (it != null) {
                this.sheet = it

                binding.apply {
                    sheetCreateTitleInputEditText.setText(it.title)

                    sheetCreateStartEditText.setText(it.start.toString())
                    sheetCreateStartEditText.setTextColor(Color.GRAY)
                    sheetCreateStartEditText.isEnabled = false

                    sheetCreateEndEditText.setText(it.end.toString())
                    sheetCreateEndEditText.setTextColor(Color.GRAY)
                    sheetCreateEndEditText.isEnabled = false

                    sheetCreateSaveButton.text = getString(R.string.edit)
                }
            }
        }

        binding.sheetCreateSaveButton.setOnClickListener {
            if (sheetId == -1L) {
                saveSheet()
                closeSheetCreate()
            } else {
                var title = binding.sheetCreateTitleInputEditText.text.toString()

                if (title.isEmpty()) {
                    title = getString(R.string.no_title)
                }

                sheet?.title = title
                sheetViewModel.updateSheet(sheet)

                closeSheetCreate()
            }
        }

        binding.sheetCreateCancelButton.setOnClickListener {
            closeSheetCreate()
        }

        binding.sheetCreateInfoImageView.setOnClickListener {
            if (sheetId == -1L) {
                guideSheetRange()
            } else {
                Snackbar.make(
                    requireView(), getString(R.string.sheet_creation_edit), Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveSheet() {
        val sheet = prepareSheet()

        if (sheet != null) {
            sheetViewModel.insert(sheet)
        }

        hideKeyboard(binding.root)
    }

    private fun prepareSheet(): Sheet? {
        val title = binding.sheetCreateTitleInputEditText.text.toString()
        val start: Int = binding.sheetCreateStartEditText.text.toString().toIntOrNull() ?: 0
        val end: Int = binding.sheetCreateEndEditText.text.toString().toIntOrNull() ?: 0

        if (start == 0 || end == 0) {
            return null
        }

        // 생성할 갯수 파악
        val diff = end - start + 1
        if (start <= end && diff <= MAX_QUESTION) {
            return Sheet(title = title, start = start, end = end)
        } else {
            Snackbar.make(binding.root, getString(R.string.max_range), Snackbar.LENGTH_LONG).show()
        }

        return null
    }

    private fun closeSheetCreate() {
        hideKeyboard(binding.root)
        this.findNavController().popBackStack()
    }

    // 시트 생성 범위 안내
    private fun guideSheetRange() {
        Snackbar.make(
            requireView(), getString(R.string.sheet_creation_range), Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun hideKeyboard(it: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}