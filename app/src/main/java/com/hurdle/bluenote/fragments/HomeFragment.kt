package com.hurdle.bluenote.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hurdle.bluenote.R
import com.hurdle.bluenote.adapters.*
import com.hurdle.bluenote.databinding.FragmentHomeBinding
import com.hurdle.bluenote.viewmodels.NoteViewModel
import com.hurdle.bluenote.viewmodels.SheetViewModel
import java.lang.Exception

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sheetAdapter: SheetAdapter

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var sheetViewModel: SheetViewModel

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

        sheetAdapter = SheetAdapter(true, OnSheetClickListener { sheet, _ ->
            val action = HomeFragmentDirections.actionNavHomeToNavSheetQuestion(
                sheet.id,
                sheet.start,
                sheet.end,
                sheet.title
            )
            this.findNavController().navigate(action)
        })

        noteAdapter = NoteAdapter(true, OnNoteClickListener { note, _, _ ->
            val action =
                HomeFragmentDirections.actionNavHomeToNavNotePage(note.id, note.title)
            this.findNavController().navigate(action)
        })


        binding.homeNoteList.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            setHasFixedSize(true)
        }

        binding.homeSheetList.apply {
            adapter = sheetAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            setHasFixedSize(true)
        }

        noteViewModel.recentNotes.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                noteAdapter.submitList(it)
            }
        }

        sheetViewModel.recentSheets.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                sheetAdapter.submitList(it)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences(
            resources.getString(R.string.preference_key),
            Context.MODE_PRIVATE
        )

        val userConsent =
            sharedPref.getBoolean(resources.getString(R.string.service_agree), false)

        if (!userConsent) {
            val inflater = LayoutInflater.from(requireActivity())
            val popupView = inflater.inflate(R.layout.popup_service_agree, null)
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            val agreeButton = popupView.findViewById<Button>(R.id.popup_agree_button)
            agreeButton.setOnClickListener {
                sharedPref
                    .edit()
                    .putBoolean(resources.getString(R.string.service_agree), true)
                    .apply()

                popupWindow.dismiss()
            }

            val finishButton = popupView.findViewById<Button>(R.id.popup_disagree_button)
            finishButton.setOnClickListener {
                requireActivity().finish()
            }

            val contentsTextView =
                popupView.findViewById<TextView>(R.id.popup_service_privacy_info_text_view)
            contentsTextView.text = getString(R.string.terms_of_service_information) + "\n\n"
            contentsTextView.append(getString(R.string.privacy_information))

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        popupWindow.showAtLocation(binding.homeNoteTextView, Gravity.CENTER, 0, 0)
                    } catch (e: Exception) {
                    }
                }
            }, 1000L)
        }
    }
}