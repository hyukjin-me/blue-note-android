package com.hurdle.bluenote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hurdle.bluenote.adapters.HomeAdapter
import com.hurdle.bluenote.data.Home
import com.hurdle.bluenote.databinding.FragmentHomeBinding
import com.hurdle.bluenote.utils.NOTE
import com.hurdle.bluenote.utils.SHEET
import com.hurdle.bluenote.viewmodels.NoteViewModel
import com.hurdle.bluenote.viewmodels.SheetViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var homeAdapter: HomeAdapter

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var sheetViewModel: SheetViewModel

    private val homes = mutableListOf<Home>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        sheetViewModel = ViewModelProvider(this).get(SheetViewModel::class.java)

        homeAdapter = HomeAdapter()

        binding.homeList.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        noteViewModel.notes.observe(viewLifecycleOwner) {
            homes.add(Home(NOTE, notes = it))
            homeAdapter.submitList(homes)
        }

        sheetViewModel.sheets.observe(viewLifecycleOwner) {
            homes.add(Home(SHEET, sheets = it))
            homeAdapter.submitList(homes)
        }
    }
}