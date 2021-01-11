package com.hurdle.bluenote.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hurdle.bluenote.R
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.databinding.FragmentSheetCreateBinding
import com.hurdle.bluenote.utils.MAX_QUESTION
import com.hurdle.bluenote.viewmodels.SheetViewModel

class SheetCreateFragment : Fragment() {

    private lateinit var binding: FragmentSheetCreateBinding
    private lateinit var sheetViewModel: SheetViewModel

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

        binding.sheetCreateSaveButton.setOnClickListener {
            saveSheet()
        }

        binding.sheetCreateCancelButton.setOnClickListener {
            cancelSheetCreate()
        }

        binding.sheetCreateInfoImageView.setOnClickListener {
            guideSheetRange()
        }
    }

    private fun saveSheet() {
        val sheet = prepareSheet()

        if (sheet != null) {
            Log.d("SHEET", "saveSheet: $sheet")
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
        }

        return null
    }

    private fun cancelSheetCreate() {
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