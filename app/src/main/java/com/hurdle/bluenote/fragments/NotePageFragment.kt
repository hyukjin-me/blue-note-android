package com.hurdle.bluenote.fragments

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
import com.hurdle.bluenote.MainActivity
import com.hurdle.bluenote.R
import com.hurdle.bluenote.adapters.NotePageAdapter
import com.hurdle.bluenote.adapters.OnNotePageClickListener
import com.hurdle.bluenote.data.NotePage
import com.hurdle.bluenote.databinding.FragmentNotePageBinding
import com.hurdle.bluenote.utils.MAX_SEARCH_LENGTH
import com.hurdle.bluenote.utils.ONE_HOUR
import com.hurdle.bluenote.viewmodels.NotePageViewModel
import com.hurdle.bluenote.viewmodels.NotePageViewModelFactory
import com.hurdle.bluenote.viewmodels.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

class NotePageFragment : Fragment() {

    companion object {
        var checkDay = ""
    }

    private lateinit var binding: FragmentNotePageBinding
    private lateinit var notePageViewModel: NotePageViewModel
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var notePageAdapter: NotePageAdapter

    private val timeHandler = Handler(Looper.getMainLooper())

    private var notePage: List<NotePage> = emptyList()
    private var isEdit = false
    private var noteId: Long = -1L
    private var title = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)

        title = NotePageFragmentArgs.fromBundle(requireArguments()).title
        noteId = NotePageFragmentArgs.fromBundle(requireArguments()).id
        setTitle(title)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotePageBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireActivity().application
        val pageFactory = NotePageViewModelFactory(application = application, noteId)
        notePageViewModel = ViewModelProvider(this, pageFactory).get(NotePageViewModel::class.java)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        displayOfWeather()
        displayOfTime()

        notePageAdapter = NotePageAdapter(OnNotePageClickListener { notePage, message ->
            if (message == "open") {
                notePageViewModel.navigateNoteOpen(notePage)
            }
        })

        binding.notePageList.apply {
            adapter = notePageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // 데이터 출력
        notePageViewModel.notePage.observe(viewLifecycleOwner) { page ->
            notePage = page
            notePageAdapter.submitList(page)

            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    // 리사이클러뷰 하단으로 이동
                    binding.notePageList.scrollToPosition(notePageAdapter.itemCount - 1)
                }
            }, 200)
        }

        // 클릭이벤트 발생시 시간/날씨 뷰 숨김
        binding.notePageCloseImageView.setOnClickListener {
            val weatherConstraintLayout = binding.notePageWeatherContainer
            weatherConstraintLayout.visibility = View.GONE
        }

        // 데이터 저장
        binding.notePageSendImageView.setOnClickListener {
            val inputTextView = binding.notePageInputEditText
            val inputText = inputTextView.text.toString()
            if (noteId != -1L && inputText.isNotEmpty()) {
                notePageViewModel.insert(NotePage(noteId = noteId, content = inputText))

                inputTextView.setText("")
            }
        }

        notePageViewModel.isEditState.observe(viewLifecycleOwner) { isEditVisible ->
            if (isEditVisible != null) {
                notePageAdapter.setEditMode(isEditVisible)
            }
        }

        notePageViewModel.navigateNoteOpen.observe(viewLifecycleOwner) { notePage ->
            if (notePage != null) {
                val action = NotePageFragmentDirections.actionNavNotePageToNavNoteOpen(
                    notePage.id,
                    notePage.noteId
                )

                this.findNavController().navigate(action)
                notePageViewModel.doneNavigateNotePage()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 다른화면 이동후 재 진입시 타이틀 라벨 변경 방지
        setTitle(title)
        notePage = emptyList()
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard(binding.root)
        // 다른 화면 이동후 노트페이지 화면으로 재진입시 기존 displayDay 변수에 저장된 날짜의 시간 뷰가 모두 GONE 처리되어 "" 처리함
        checkDay = ""
        isEdit = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.findItem(R.id.menu_edit).isVisible = true

        menu.findItem(R.id.menu_search).isVisible = true
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        val searchViewEditText: EditText
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            setIconifiedByDefault(true)
            searchViewEditText =
                this.findViewById(R.id.search_src_text)
            // 글자수 제한
            searchViewEditText.filters = arrayOf(InputFilter.LengthFilter(MAX_SEARCH_LENGTH))

            this.setOnQueryTextFocusChangeListener { _, hasFocus ->
                when (hasFocus) {
                    true -> {
                    }
                    false -> {
                        notePageAdapter.submitList(notePage)
                    }
                }
            }

            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrEmpty()) {
                        val searchResult = notePage.filter { it.content.contains(query) }
                        notePageAdapter.submitList(searchResult)
                    }
                    hideKeyboard(binding.root)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val inputText = newText ?: ""
                    if (inputText.count() >= MAX_SEARCH_LENGTH) {
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
                isEdit = !isEdit

                if (isEdit) {
                    item.setIcon(R.drawable.ic_baseline_edit_24_red)
                } else {
                    item.setIcon(R.drawable.ic_baseline_edit_24)
                }

                notePageViewModel.changeEditState(isEdit)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hideKeyboard(it: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    private fun setTitle(title: String) {
        val activity = activity as MainActivity
        activity.setToolbarTitle(title)
    }

    private fun displayOfWeather() {
        // 날씨 텍스트 데이터 가져올때까지 숨김
        val weatherTextView = binding.notePageWeatherTextView

        weatherViewModel.currentWeather.observe(viewLifecycleOwner) { weather ->

            if (weather == null) {
                // 날씨 API 호출
                weatherViewModel.getWeather()
                weatherTextView.alpha = 0f
            } else {
                // 현재시간과 db에 기록된 시차차이가 1시간 이상 차이가 난다면 업데이트 요청
                val diffTime = System.currentTimeMillis() - weather.time
                if (diffTime > ONE_HOUR) {
                    weatherViewModel.getWeather()
                }

                val temperature: String = weather.temperature // °C
                val atmosphere: String = weather.atmosphere
                val location: String = weather.location

                weatherTextView.text = String.format(
                    getString(R.string.display_weather),
                    temperature,
                    atmosphere,
                    location
                )

                weatherTextView.visibility = View.VISIBLE
                weatherTextView.animate().alpha(1f).apply {
                    duration = 2000
                }.start()
            }
        }
    }

    private fun displayOfTime() {
        // 실시간 시간표시
        // https://stackoverflow.com/questions/55570990/kotlin-call-a-function-every-second
        timeHandler.post(object : Runnable {
            override fun run() {
                val calendar = Calendar.getInstance()
                val fullTimeFormat =
                    SimpleDateFormat("yyyy-MM-dd (E) aa HH:mm:ss", Locale.getDefault())
                val displayOfDate = fullTimeFormat.format(calendar.time)
                binding.notePageTimeTextView.text = displayOfDate

                timeHandler.postDelayed(this, 1000)
            }
        })
    }
}