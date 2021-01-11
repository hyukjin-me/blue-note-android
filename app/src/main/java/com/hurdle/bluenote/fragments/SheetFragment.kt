package com.hurdle.bluenote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hurdle.bluenote.databinding.FragmentSheetBinding
import com.hurdle.bluenote.viewmodels.NoteViewModel

class SheetFragment : Fragment() {

    private lateinit var binding: FragmentSheetBinding

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        binding.sheetAddFab.setOnClickListener {
            // 시트생성화면으로 이동
            navigateCreatePage()
        }
    }

    private fun navigateCreatePage() {
        this.findNavController().navigate(SheetFragmentDirections.actionNavSheetToNavSheetCreate())
    }
}