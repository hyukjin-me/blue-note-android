package com.hurdle.bluenote.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hurdle.bluenote.MainActivity
import com.hurdle.bluenote.R
import com.hurdle.bluenote.adapters.NotePageAdapter
import com.hurdle.bluenote.adapters.OnNotePageClickListener
import com.hurdle.bluenote.data.NotePage
import com.hurdle.bluenote.databinding.FragmentNotePageBinding
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

    val timeHandler = Handler(Looper.getMainLooper())

    private var noteId: Long = -1L

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val title = NotePageFragmentArgs.fromBundle(requireArguments()).title
        noteId = NotePageFragmentArgs.fromBundle(requireArguments()).id
        val activity = activity as MainActivity
        activity.setToolbarTitle(title)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotePageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireActivity().application
        val wordFactory = NotePageViewModelFactory(application = application, noteId)
        notePageViewModel = ViewModelProvider(this, wordFactory).get(NotePageViewModel::class.java)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        displayOfWeather()
        displayOfTime()

        notePageAdapter = NotePageAdapter(OnNotePageClickListener {

        })

        binding.noteWordList.apply {
            adapter = notePageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // 데이터 출력
        notePageViewModel.notePage.observe(viewLifecycleOwner) { page ->
            notePageAdapter.submitList(page)

            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    // 리사이클러뷰 하단으로 이동
                    binding.noteWordList.scrollToPosition(notePageAdapter.itemCount - 1)
                }
            }, 200)
        }

        // 클릭이벤트 발생시 시간/날씨 뷰 숨김
        binding.noteWordCloseImageView.setOnClickListener {
            val weatherConstraintLayout = binding.noteWordWeatherContainer
            weatherConstraintLayout.visibility = View.GONE
        }

        // 데이터 저장
        binding.noteWordSendImageView.setOnClickListener {
            val inputTextView = binding.noteWordInputEditText
            val inputText = inputTextView.text.toString()
            if (noteId != -1L && inputText.isNotEmpty()) {
                notePageViewModel.insert(NotePage(noteId = noteId, content = inputText))

                inputTextView.setText("")
            }
        }
    }

    private fun displayOfWeather() {
        // 날씨 텍스트 데이터 가져올때까지 숨김
        val weatherTextView = binding.noteWordWeatherTextView

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
                binding.noteWordTimeTextView.text = displayOfDate

                timeHandler.postDelayed(this, 1000)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard(binding.root)
        // 다른 화면 이동후 노트페이지 화면으로 재진입시 기존 displayDay 변수에 저장된 날짜의 시간 뷰가 모두 GONE 처리되어 "" 처리함
        checkDay = ""
    }

    private fun hideKeyboard(it: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}