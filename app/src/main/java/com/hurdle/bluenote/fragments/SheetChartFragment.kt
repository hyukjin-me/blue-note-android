package com.hurdle.bluenote.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.hurdle.bluenote.MainActivity.Companion.mInterstitialAd
import com.hurdle.bluenote.R
import com.hurdle.bluenote.adapters.SheetChartAdapter
import com.hurdle.bluenote.data.Question
import com.hurdle.bluenote.data.Sheet
import com.hurdle.bluenote.databinding.FragmentSheetChartBinding
import com.hurdle.bluenote.viewmodels.SheetQuestionViewModel
import com.hurdle.bluenote.viewmodels.SheetQuestionViewModelFactory
import com.hurdle.bluenote.viewmodels.SheetViewModel
import java.text.SimpleDateFormat
import java.util.*

class SheetChartFragment : Fragment() {

    private lateinit var binding: FragmentSheetChartBinding

    private lateinit var questionViewModel: SheetQuestionViewModel
    private lateinit var sheetViewModel: SheetViewModel
    private lateinit var chartAdapter: SheetChartAdapter

    private lateinit var sheet: Sheet

    private var sheetId = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSheetChartBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sheetChartProgressBar.visibility = View.VISIBLE
        binding.sheetChartLineChart.visibility = View.INVISIBLE

        sheetId = SheetChartFragmentArgs.fromBundle(requireArguments()).sheetId

        val application = requireNotNull(activity).application
        val factory = SheetQuestionViewModelFactory(application, sheetId)
        questionViewModel = ViewModelProvider(this, factory).get(SheetQuestionViewModel::class.java)

        sheetViewModel = ViewModelProvider(this).get(SheetViewModel::class.java)

        sheetViewModel.getSheet(sheetId)

        chartAdapter = SheetChartAdapter()

        binding.sheetChartList.apply {
            adapter = chartAdapter
            layoutManager = GridLayoutManager(requireContext(), 10)
            setHasFixedSize(true)
        }

        sheetViewModel.sheet.observe(viewLifecycleOwner) { sheet ->
            if (sheet == null) {
                // 데이터가 없을경우 뒤로가기
                this.findNavController().popBackStack()

                // 에러 메세지
                Snackbar.make(requireView(), getString(R.string.error), Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                this.sheet = sheet

                binding.sheetChartTitleTextView.text =
                    String.format(
                        getString(R.string.display_chart_title),
                        sheet.title,
                        sheet.totalTime
                    )

                showAd()
            }
        }

        questionViewModel.questions.observe(viewLifecycleOwner) { questions ->
            prepareChart(questions)

            chartAdapter.submitList(questions)
        }

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("광고", "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d("광고", "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("광고", "Ad showed fullscreen content.")
                mInterstitialAd = null
            }
        }
    }

    private fun showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(requireActivity())
        } else {
            Log.d("광고", "The interstitial ad wasn't ready yet.");
        }
    }

    // Chart, 01:30 를 60s+30s 연산하여 90s을 리턴함
    private fun getSecond(targetTime: String): Float {
        val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.KOREA)
        val data = simpleDateFormat.parse(targetTime)

        if (data != null) {
            val minuteDataFormat = SimpleDateFormat("mm", Locale.KOREA)
            val secondDataFormat = SimpleDateFormat("ss", Locale.KOREA)

            val second = secondDataFormat.format(data).toFloat()
            val minute = minuteDataFormat.format(data).toFloat()

            return second + (minute * 60)
        }

        return 0f
    }

    private fun prepareChart(questions: List<Question>) {
        val start = questions[0].number
        val end = questions[questions.size - 1].number
        val count = end - start + 1

        val answer = questions.filter { it.isAnswer }
        val wrong = questions.filter { !it.isAnswer }

        val answerList = mutableListOf<Entry>()
        val wrongList = mutableListOf<Entry>()

        binding.sheetChartInfoTextView.text =
            String.format(getString(R.string.display_chart_info), count, answer.size, wrong.size)

        answer.forEach { question ->
            val x = question.number.toFloat()
            val y = getSecond(question.time)
            answerList.add(Entry(x, y))
        }

        wrong.forEach { question ->
            val x = question.number.toFloat()
            val y = getSecond(question.time)
            wrongList.add(Entry(x, y))
        }

        val correctAnswerLineDataSet = LineDataSet(answerList, getString(R.string.correct))
        correctAnswerLineDataSet.apply {
            color = resources.getColor(R.color.blue_btn)
            setCircleColor(Color.BLUE)
            valueTextColor = Color.WHITE
        }

        val wrongAnswerLineDataSet = LineDataSet(wrongList, getString(R.string.wrong))
        wrongAnswerLineDataSet.apply {
            color = Color.RED
            setCircleColor(Color.RED)
            valueTextColor = Color.WHITE
        }

        val lineData = LineData(correctAnswerLineDataSet, wrongAnswerLineDataSet)

        drawChart(lineData)
    }

    private fun drawChart(lineData: LineData) {
        binding.sheetChartLineChart.apply {
            data = lineData

            // 배경색
            val color = ContextCompat.getColor(requireContext(), R.color.blue_text)
            setBackgroundColor(color)

            // X축 바닥으로 이동 (default : top)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            // X축 custom
            xAxis.valueFormatter = AxisXFormatter()
            // X축 텍스트 색상
            xAxis.textColor = Color.WHITE

            // y축 왼쪽
            axisLeft.textColor = Color.WHITE

            // y축 오른쪽 비활성
            axisRight.setDrawLabels(false)

            legend.textColor = Color.WHITE
            legend.textSize = 10f
            legend.horizontalAlignment
            legend.xEntrySpace = 18f

            description.text = ""
            description.isEnabled = false

            invalidate()
            visibility = View.VISIBLE
            binding.sheetChartProgressBar.visibility = View.INVISIBLE
        }
    }

    class AxisXFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return value.toInt().toString()
        }
    }

    override fun onPause() {
        super.onPause()
    }
}