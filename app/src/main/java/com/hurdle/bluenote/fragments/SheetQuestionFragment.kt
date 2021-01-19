package com.hurdle.bluenote.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.Button
import android.widget.Chronometer
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hurdle.bluenote.MainActivity
import com.hurdle.bluenote.R
import com.hurdle.bluenote.adapters.OnQuestionClickListener
import com.hurdle.bluenote.adapters.SheetQuestionAdapter
import com.hurdle.bluenote.data.Question
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.databinding.FragmentSheetQuestionBinding
import com.hurdle.bluenote.utils.ChronometerManager
import com.hurdle.bluenote.utils.ChronometerManager.continueChronometer
import com.hurdle.bluenote.utils.ChronometerManager.reBaseChronometer
import com.hurdle.bluenote.utils.ChronometerManager.resetChronometer
import com.hurdle.bluenote.utils.ChronometerManager.resetElapsedRealTime
import com.hurdle.bluenote.utils.ChronometerManager.startFirstChronometer
import com.hurdle.bluenote.utils.ChronometerManager.stopChronometer
import com.hurdle.bluenote.utils.colorBlendFilter
import com.hurdle.bluenote.viewmodels.SheetQuestionViewModel
import com.hurdle.bluenote.viewmodels.SheetQuestionViewModelFactory
import com.hurdle.bluenote.viewmodels.SheetViewModel
import java.text.SimpleDateFormat
import java.util.*

class SheetQuestionFragment : Fragment() {

    private lateinit var binding: FragmentSheetQuestionBinding

    private lateinit var sheetViewModel: SheetViewModel
    private lateinit var questionViewModel: SheetQuestionViewModel

    private lateinit var startButton: Button
    private lateinit var totalChronometer: Chronometer
    private lateinit var currentChronometer: Chronometer

    private lateinit var sheetQuestionAdapter: SheetQuestionAdapter

    private var sheetId: Long = -1L
    private var sheet: Sheet? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val title = SheetQuestionFragmentArgs.fromBundle(requireArguments()).title
        val mainActivity = activity as MainActivity
        mainActivity.setToolbarTitle(title)
    }

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

        startButton = binding.sheetQuestionStartButton
        totalChronometer = binding.sheetQuestionTotalChronometer
        currentChronometer = binding.sheetQuestionCurrentChronometer

        sheetId = SheetQuestionFragmentArgs.fromBundle(requireArguments()).sheetId
        val startNumber = SheetQuestionFragmentArgs.fromBundle(requireArguments()).start
        val endNumber = SheetQuestionFragmentArgs.fromBundle(requireArguments()).end

        sheetViewModel = ViewModelProvider(this).get(SheetViewModel::class.java)
        sheetViewModel.getSheet(sheetId)

        val application = requireNotNull(activity).application
        val factory = SheetQuestionViewModelFactory(application, sheetId)
        questionViewModel = ViewModelProvider(this, factory).get(SheetQuestionViewModel::class.java)

        sheetQuestionAdapter = SheetQuestionAdapter(OnQuestionClickListener { question: Question ->
            // 진행시간 업데이트
            val currentTimeText = currentChronometer.text.toString()

            val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
            val oldTime: Date? = formatter.parse(question.time)
            val newTime: Date? = formatter.parse(currentTimeText)

            if (oldTime != null && newTime != null) {
                // 기존시간 + 추가된 시간
                newTime.time += oldTime.time

                val currentTime: String = formatter.format(newTime)
                question.time = currentTime
            }

            // "STOP" 버튼, 크로노미터 작동중, 0부터 다시 카운트 시작
            if (startButton.text == resources.getText(R.string.stop)) {
                resetChronometer(currentChronometer)
            } else if (startButton.text == resources.getText(R.string.start)) {
                // "START" 버튼, 크로노미터가 작동중이 아님, 0으로 변경
                currentChronometer.base = SystemClock.elapsedRealtime()
            }

            // 데이터 업데이트
            questionViewModel.update(question)

            // 데이터 갱신
            sheetQuestionAdapter.notifyDataSetChanged()
        })

        binding.sheetQuestionList.apply {
            adapter = sheetQuestionAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // 아이템 버튼 클릭시 뷰 전체가 깜빡이는 현상 제거
            itemAnimator = null
            setHasFixedSize(true)
        }

        startButton.setOnClickListener {
            startSheet()
        }

        sheetViewModel.sheet.observe(viewLifecycleOwner) { sheet ->
            if (sheet != null) {
                this.sheet = sheet

                // 크로노미터 시간 db 데이터로 초기화
                reBaseChronometer(totalChronometer, sheet.totalTime)
            }
        }

        questionViewModel.questions.observe(viewLifecycleOwner) { questions ->
            if (questions.count() == 0) {
                val questionList = mutableListOf<Question>()

                (startNumber..endNumber).forEach { number ->
                    questionList.add(Question(number = number, sheetId = sheetId))
                }

                questionViewModel.insert(questionList)
            } else {
                sheetQuestionAdapter.submitList(questions)
            }
        }
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

    override fun onResume() {
        super.onResume()
        resetElapsedRealTime()
    }

    override fun onPause() {
        super.onPause()
        stopProgress()
    }

    private fun startSheet() {
        // 화면진입후 10초 대기 시작버튼 클릭시 시작이 10초로 변경됨

        // 시작버튼 클릭
        if (startButton.text == resources.getText(R.string.start)) {
            // 버튼 텍스트 "정지"으로 변경
            startButton.text = resources.getText(R.string.stop)

            // 앱 종료이후 화면 처음으로 진입시 lastElapsedRealTime 값은 0 이다.
            if (ChronometerManager.lastElapsedRealTime == 0L) {
                if (totalChronometer.text == "00:00") {
                    // 시작 버튼을 처음 클릭, 크로노미터 00:00 부터 시작
                    startFirstChronometer(totalChronometer)
                } else {
                    // 화면 진입은 처음이지만 기존 데이터가 존재, 이어서 시작
                    val storedTime: String = totalChronometer.text.toString()

                    reBaseChronometer(totalChronometer, storedTime).start()
                }
            } else {
                // 재시작, 크로노미터가 중지된 상태이기 때문에 이어서 시작
                continueChronometer(totalChronometer)
            }

            // current 크로노미터는 버튼 터치시마다 매번 00:00 으로 갱신
            startFirstChronometer(currentChronometer)
        } else {
            stopProgress()
        }
    }

    private fun stopProgress() {
        // 버튼 텍스트 "시작"으로 변경
        startButton.text = resources.getText(R.string.start)

        // 크로노미터 정지
        stopChronometer(currentChronometer)
        stopChronometer(totalChronometer)

        // 전체 진행시간 저장
        saveTotalProgress()
    }

    private fun saveTotalProgress() {
        if (sheet != null) {
            val totalChronometerText = totalChronometer.text.toString()
            this.sheet?.totalTime = totalChronometerText

            sheetViewModel.updateSheet(sheet)
        }
    }
}