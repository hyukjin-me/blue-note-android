package com.hurdle.bluenote.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hurdle.bluenote.R
import com.hurdle.bluenote.adapters.OnSheetClickListener
import com.hurdle.bluenote.adapters.SheetAdapter
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.databinding.FragmentSheetBinding
import com.hurdle.bluenote.viewmodels.NoteViewModel
import com.hurdle.bluenote.viewmodels.SheetViewModel

class SheetFragment : Fragment() {

    private lateinit var binding: FragmentSheetBinding

    private lateinit var sheetViewModel: SheetViewModel
    private lateinit var sheetAdapter: SheetAdapter

    private var sheetList: List<Sheet> = emptyList()

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
            if (!isLongClick) {
            } else {
            }
        })

        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.sheetList.apply {
            adapter = sheetAdapter
            layoutManager = linearLayoutManager
        }

        sheetViewModel.sheets.observe(viewLifecycleOwner) { sheets ->
            this.sheetList = sheets
            sheetAdapter.submitList(sheets)

            // https://dreamaz.tistory.com/249
            // 안드로이드 RecyclerView scrollToPosition이 제대로 동작하지 않을 때
            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    binding.sheetList.scrollToPosition(0)
                }
            }, 200)
        }

        binding.sheetAddFab.setOnClickListener {
            // 시트생성화면으로 이동
            navigateCreatePage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val editMenu = menu.findItem(R.id.menu_edit)
        editMenu.isVisible = true

        val deleteMenu = menu.findItem(R.id.menu_delete)
        deleteMenu.isVisible = true

        val searchMenu = menu.findItem(R.id.menu_search)
        searchMenu.isVisible = true
    }

    private fun navigateCreatePage() {
        this.findNavController().navigate(SheetFragmentDirections.actionNavSheetToNavSheetCreate())
    }
}