package com.hurdle.bluenote.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.hurdle.bluenote.R
import com.hurdle.bluenote.databinding.FragmentSheetQuestionBinding
import com.hurdle.bluenote.utils.colorBlendFilter

class SheetQuestionFragment : Fragment() {

    private lateinit var binding: FragmentSheetQuestionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSheetQuestionBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // 기존 툴바 메뉴 숨김
        menu.findItem(R.id.menu_edit).isVisible = false
        menu.findItem(R.id.menu_search).isVisible = false
        menu.findItem(R.id.menu_edit).isVisible = false
        menu.findItem(R.id.menu_delete).isVisible = false


        // 새로운 메뉴
        inflater.inflate(R.menu.quetion_toolbar_menu, menu)
        menu.forEach { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_chart -> menuItem.icon.colorFilter =
                    colorBlendFilter(Color.WHITE)

                R.id.menu_helper -> menuItem.icon.colorFilter =
                    colorBlendFilter(Color.WHITE)
            }
        }
    }
}