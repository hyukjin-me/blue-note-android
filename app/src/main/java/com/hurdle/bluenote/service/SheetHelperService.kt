package com.hurdle.bluenote.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hurdle.bluenote.MainActivity
import com.hurdle.bluenote.R
import com.hurdle.bluenote.adapters.OnQuestionClickListener
import com.hurdle.bluenote.adapters.SheetHelperAdapter
import com.hurdle.bluenote.data.*
import com.hurdle.bluenote.databinding.ServiceHelperIconBinding
import com.hurdle.bluenote.databinding.ServiceHelperMoreBinding
import com.hurdle.bluenote.databinding.ServiceHelperSheetBinding
import com.hurdle.bluenote.utils.ChronometerManager.continueChronometer
import com.hurdle.bluenote.utils.ChronometerManager.reBaseChronometer
import com.hurdle.bluenote.utils.ChronometerManager.resetChronometer
import com.hurdle.bluenote.utils.ChronometerManager.startFirstChronometer
import com.hurdle.bluenote.utils.ChronometerManager.stopChronometer
import com.hurdle.bluenote.utils.HelperConstants.EXPANSION_VIEW
import com.hurdle.bluenote.utils.HelperConstants.HELPER_FOREGROUND_STATE
import com.hurdle.bluenote.utils.HelperConstants.HELPER_STOP
import com.hurdle.bluenote.utils.HelperConstants.HELPER_VIEW_STATE
import com.hurdle.bluenote.utils.HelperConstants.ICON_VIEW
import com.hurdle.bluenote.utils.HelperConstants.INIT_NONE
import com.hurdle.bluenote.utils.HelperConstants.INIT_PREPARE
import com.hurdle.bluenote.utils.HelperConstants.NOTIFICATION_CHANNEL_ID
import com.hurdle.bluenote.utils.HelperConstants.NOTIFICATION_ID
import com.hurdle.bluenote.utils.HelperConstants.PLAY_TAG
import com.hurdle.bluenote.utils.HelperConstants.SHEET_ID
import com.hurdle.bluenote.utils.HelperConstants.STOP_TAG
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

class SheetHelperService : Service() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var helperAdapter: SheetHelperAdapter

    private lateinit var bindingIcon: ServiceHelperIconBinding
    private lateinit var bindingSheet: ServiceHelperSheetBinding

    private lateinit var notificationManager: NotificationManager

    private lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams

    private val windowType: Int =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

    // 터치이벤트 관련 변수
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0.toFloat()
    private var initialTouchY = 0.toFloat()
    private var lastAction = 0

    // 크로노미터
    private var isPlayTimer = false
    private var lastStopElapsedRealtime: Long = 0L

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private lateinit var sheetDao: SheetDao
    private lateinit var questionDao: QuestionDao

    private lateinit var sheet: Sheet
    private lateinit var questions: List<Question>
    private var sheetId: Long = -1L

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        Log.d("서비스", "onCreate() 호출됨")
        createHelper()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("서비스", "onStartCommand() 호출됨")

        if (intent == null || intent.action == null) {
            finishHelperService()
            return START_NOT_STICKY
        }

        if (intent.action == HELPER_STOP) {
            finishHelperService()
            return START_NOT_STICKY
        }

        if (HELPER_FOREGROUND_STATE == INIT_NONE) {
            HELPER_VIEW_STATE = ICON_VIEW
        }

        sheetId = intent.getLongExtra(SHEET_ID, -1L)

        HELPER_FOREGROUND_STATE = INIT_PREPARE

        startForeground(NOTIFICATION_ID, prepareNotification())

        return START_NOT_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createHelper() {
        sheetDao = NoteDatabase.getDatabase(applicationContext).sheetDao
        questionDao = NoteDatabase.getDatabase(applicationContext).questionDao

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER

        val inflater = LayoutInflater.from(applicationContext)
        bindingIcon = ServiceHelperIconBinding.inflate(inflater)
        bindingSheet = ServiceHelperSheetBinding.inflate(inflater)

        windowManager.addView(bindingIcon.root, params)

        val circleIconImageView = bindingIcon.helperIconImageView
        // 아이콘 클릭 이벤트
        circleIconImageView.setOnTouchListener { view, event ->
            val root = bindingIcon.root

            touchMotion(event, root, view)

            HELPER_VIEW_STATE = EXPANSION_VIEW

            true
        }

        val closeImageView = bindingSheet.helperSheetCloseImageView
        // 닫기 버튼 클릭 이벤트
        closeImageView.setOnClickListener {
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT

            windowManager.addView(bindingIcon.root, params)
            windowManager.removeView(bindingSheet.root)

            val totalChronometer = bindingSheet.helperSheetTotalTimeChronometer
            val currentChronometer = bindingSheet.helperSheetCurrentTimeChronometer

            stopChronometer(totalChronometer)
            stopChronometer(currentChronometer)

            val playButton = bindingSheet.helperSheetPlayButton
            playButton.tag = PLAY_TAG
            playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

            coroutineScope.launch(Dispatchers.IO) {
                val totalTimeText = totalChronometer.text.toString()
                sheet.totalTime = totalTimeText

                sheetDao.update(sheet)
            }

            isPlayTimer = false

            HELPER_VIEW_STATE = ICON_VIEW
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        job.cancel()

        HELPER_FOREGROUND_STATE = INIT_NONE

        Log.d("서비스", "onDestroy() 호출됨")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private suspend fun getSheet(sheetDao: SheetDao): Sheet {
        return withContext(Dispatchers.IO) {
            val sheet = sheetDao.get(sheetId)
            sheet
        }
    }

    private suspend fun getQuestions(questionDao: QuestionDao): List<Question> {
        return withContext(Dispatchers.IO) {
            val questions = questionDao.getQuestions(sheetId)
            questions
        }
    }

    private fun finishHelperService() {
        stopForeground(true)
        stopSelf()

        try {
            if (HELPER_VIEW_STATE == ICON_VIEW) {
                windowManager.removeView(bindingIcon.root)
            } else {
                windowManager.removeView(bindingSheet.root)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun touchMotion(
        event: MotionEvent,
        root: ConstraintLayout,
        view: View
    ) {
        when (event.action) {
            // 기기에 손이 닿았을때 처리
            MotionEvent.ACTION_DOWN -> {
                actionDown(event)
            }

            // 기게에서 손이 떨어졌을때 처리
            MotionEvent.ACTION_UP -> {
                actionUp(root)
                view.performClick()
            }

            // 기기에서 닿은상태로 손이 움직였을때 처리
            MotionEvent.ACTION_MOVE -> {
                actionMove(event, root)
            }
        }
    }

    // MotionEvent.ACTION_DOWN 이벤트 처리
    private fun actionDown(event: MotionEvent) {
        initialX = params.x
        initialY = params.y
        initialTouchX = event.rawX
        initialTouchY = event.rawY

        // ACTION_UP, ACTION_MOVE를 구분하기 위한 변수
        lastAction = MotionEvent.ACTION_DOWN
    }

    // MotionEvent.ACTION_UP 이벤트 처리
    private fun actionUp(root: ConstraintLayout) {
        if (lastAction == MotionEvent.ACTION_DOWN) {
            // Mini Icon일때 처리
            if (bindingIcon.root == root) {
                // 디바이스 크기 가져오기
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)

                // val height = displayMetrics.heightPixels
                val width = displayMetrics.widthPixels

                // 사이즈 설정
                params.width = (width * 0.9f).roundToInt()
                // params.height = (height * 0.2f).roundToInt()
                params.height = WindowManager.LayoutParams.WRAP_CONTENT

                // 뷰 교체
                windowManager.addView(bindingSheet.root, params)
                windowManager.removeViewImmediate(bindingIcon.root)

                initSheetView()
            }
        } else if (lastAction == MotionEvent.ACTION_MOVE) {
            // ...
        }

        lastAction = MotionEvent.ACTION_UP

        // 화면 알파값 정상화
        root.alpha = 1f
    }

    // MotionEvent.ACTION_MOVE 이벤트 처리
    private fun actionMove(event: MotionEvent, root: ConstraintLayout) {
        // 실제 기기의 물리적 크기
        val point = Point()
        val display = windowManager.defaultDisplay
        display?.getRealSize(point)

        // 스크린 (물리적 화면)에서의 뷰 좌표
        // https://stackoverflow.com/questions/17672891/getlocationonscreen-vs-getlocationinwindow
        val location = intArrayOf(0, 0)
        root.getLocationOnScreen(location)

        // X position
        if (location[0] == 0) {
            // 뷰의 왼쪽 최대 이동 위치
            val paramStartX = initialX + (event.rawX - initialTouchX).toInt()
            if (params.x < paramStartX) {
                params.x = paramStartX
            }
        } else if (location[0] + root.width == point.x) {
            // 뷰의 오른쪽 최대 이동 위치
            val paramEndX = initialX + (event.rawX - initialTouchX).toInt()
            if (params.x > paramEndX) {
                params.x = paramEndX
            }
        } else {
            params.x = initialX + (event.rawX - initialTouchX).toInt()
        }

        // 상단, 상태바 높이 구하기
        // https://www.tutorialspoint.com/what-is-the-height-of-the-status-bar-in-android
        val idStatusBarHeight: Int =
            resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusHeight = if (idStatusBarHeight > 0) {
            // Status Bar Height
            resources.getDimensionPixelSize(idStatusBarHeight)
        } else {
            // Resources NOT found
            0
        }

        // 하단, 네비게이션바 높이 구하기
        // https://stackoverflow.com/questions/29398929/how-get-height-of-the-status-bar-and-soft-key-buttons-bar
        val idNavigationHeight: Int =
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val navigationBarHeight = if (idNavigationHeight > 0) {
            // NavigationBar Height
            resources.getDimensionPixelSize(idNavigationHeight)
        } else {
            // Resources NOT found
            0
        }

        // Y position 처리
        if (statusHeight != 0 && location[1] == statusHeight) {
            // 뷰가 갈 수 있는 최대 상단 높이, statusBar 까지 제한함
            val paramTopY = initialY + (event.rawY - initialTouchY).toInt()
            if (params.y < paramTopY) {
                params.y = paramTopY
            }
        } else if (point.y == (location[1] + root.height + navigationBarHeight)) {
            // 뷰가 갈 수 있는 최대 하단 높이, NavigationBar(백버튼 홈버튼) 까지 제한함
            // 예) 1280(Device) = 354(View Height) + 842(Location[1]) + 84(NavigationBar Height)
            val paramBottomY = initialY + (event.rawY - initialTouchY).toInt()
            if (params.y > paramBottomY) {
                params.y = paramBottomY
            }
        } else {
            params.y = initialY + (event.rawY - initialTouchY).toInt()
        }

        // 뷰 업데이트
        windowManager.updateViewLayout(root, params)

        // 뷰액션 처리
        // ACTION_DONW -> ACTION_UP 을 원하지만 약간의 움직임으로 인해서
        // lastAction 변수가 ACTION_MOVE 로 인식되는걸 방지를 위해 설정
        // 움직인(터치한) 거리가 크지 않을경우 ACTION_MOVE 가 아니라 lastAction을 ACTION_UP 으로 판단
        val xDragCheck = abs(abs(initialX) - abs(params.x))
        val yDragCheck = abs(abs(initialY) - abs(params.y))
        // 뷰가 움직이는 최소거리
        val area = 10
        // 최소거리보다 움직임이 큰경우에 이동한것으로 처리한다.
        if (xDragCheck > area && yDragCheck > area) {
            lastAction = MotionEvent.ACTION_MOVE

            // 유저에게 뷰가 이동가능하다는걸 보여주기 위한 알파값설정
            root.alpha = 0.4f
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSheetView() {
        val playButtonImage: ImageView = bindingSheet.helperSheetPlayButton
        val totalChronometer = bindingSheet.helperSheetTotalTimeChronometer
        val currentChronometer = bindingSheet.helperSheetCurrentTimeChronometer
        val titleTextView = bindingSheet.helperSheetTitleTextView

        // 서비스 처음 실행시 중지 상태이므로 stop 으로 태그 설정
        playButtonImage.tag = STOP_TAG

        helperAdapter = SheetHelperAdapter(OnQuestionClickListener { question: Question ->

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

            // 진행중일때, '현재' 크로노미터 초기화 및 재시작
            if (playButtonImage.tag == STOP_TAG) {
                resetChronometer(currentChronometer)
            } else {
                // 진행중이 아닐때, '현재' 크로노미터 초기화
                currentChronometer.base = SystemClock.elapsedRealtime()
            }

            coroutineScope.launch(Dispatchers.IO) {
                questionDao.update(question)
            }

            helperAdapter.notifyDataSetChanged()
        })

        linearLayoutManager = LinearLayoutManager(this)

        bindingSheet.helperSheetList.apply {
            adapter = helperAdapter
            layoutManager = linearLayoutManager
            itemAnimator = null
            setHasFixedSize(true)
        }

        bindingSheet.helperSheetList.height

        coroutineScope.launch {
            sheet = getSheet(sheetDao)

            titleTextView.text = sheet.title

            // 전체 크로노미터 초기화
            reBaseChronometer(totalChronometer, sheet.totalTime)
        }

        coroutineScope.launch {
            questions = getQuestions(questionDao)

            helperAdapter.submitList(questions)
        }

        // 다음 문제로 이동 버튼
        bindingSheet.helperSheetNextButton.setOnClickListener {
            val position = linearLayoutManager.findFirstVisibleItemPosition() + 1
            linearLayoutManager.scrollToPosition(position)
        }

        // 이전 문제로 이동 버튼
        bindingSheet.helperSheetPreviousButton.setOnClickListener {
            val position = linearLayoutManager.findFirstVisibleItemPosition() - 1
            linearLayoutManager.scrollToPosition(position)
        }

        playButtonImage.setOnClickListener {
            if (!isPlayTimer) {
                // 크로노미터 시작

                // 일시중지 버튼으로 변경
                playButtonImage.tag = STOP_TAG
                playButtonImage.setImageResource(R.drawable.ic_baseline_pause_24)

                if (lastStopElapsedRealtime == 0L) {
                    if (totalChronometer.text == "00:00") {
                        // 시작 버튼을 처음 클릭, 크로노미터 00:00 부터 시작
                        startFirstChronometer(totalChronometer)
                    } else {
                        // 화면 진입은 처음이지만 기존 데이터가 존재, 이어서 시작
                        val storedTime: String = totalChronometer.text.toString()

                        reBaseChronometer(totalChronometer, storedTime).start()
                    }
                } else {
                    continueChronometer(totalChronometer)
                }

                startFirstChronometer(currentChronometer)
            } else {
                playButtonImage.tag = PLAY_TAG
                playButtonImage.setImageResource(R.drawable.ic_baseline_play_arrow_24)

                stopChronometer(totalChronometer)
                stopChronometer(currentChronometer)

                coroutineScope.launch(Dispatchers.IO) {
                    val totalTimeText = totalChronometer.text.toString()
                    sheet.totalTime = totalTimeText

                    sheetDao.update(sheet)
                }
            }

            isPlayTimer = !isPlayTimer
        }

        bindingSheet.helperSheetMoreImageView.setOnClickListener {
            val inflater = LayoutInflater.from(this.applicationContext)
            val bindingMore = ServiceHelperMoreBinding.inflate(inflater, bindingSheet.root, true)

            bindingMore.helperMoreHome.setOnClickListener {
                bindingMore.root.removeAllViews()
            }

            bindingMore.helperMoreClose.setOnClickListener {
                windowManager.removeView(bindingSheet.root)
                stopForeground(true)
                this.stopSelf()
            }

            bindingMore.root.setOnTouchListener { _, _ ->
                bindingMore.root.removeAllViews()
                false
            }
        }

        bindingSheet.helperSheetTopContainer.setOnTouchListener { view, event ->
            val root = bindingSheet.root
            touchMotion(event, root, view)
            true
        }
    }

    private fun prepareNotification(): Notification {
        // PendingIntent 생성
        // https://developer.android.com/guide/components/services?hl=ko#Foreground
        val pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            notificationIntent.action = INIT_NONE
            PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val stopIntent = Intent(this, SheetHelperService::class.java).let { stopIntent ->
            stopIntent.action = HELPER_STOP
            PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val remoteViews = RemoteViews(packageName, R.layout.service_helper_notification)
        remoteViews.setOnClickPendingIntent(R.id.helper_notification_close_button, stopIntent)

        // 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getText(R.string.notification_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(channel)
        }

        // 빌더 생성
        val notificationBuilder: NotificationCompat.Builder =
            // 채널 체크
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            } else {
                NotificationCompat.Builder(this)
            }

        notificationBuilder
            .setContent(remoteViews)
            .setSmallIcon(R.drawable.ic_baseline_assistant_24)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setOnlyAlertOnce(true) // 1번만 알림
            .setOngoing(true) // X버튼을 눌러야 사라짐
            .setAutoCancel(true)
            // Notification 터치시 MainActivity 이동
            .setContentIntent(pendingIntent)

        // 락스크린에서 모든 내용이 보이게 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

        // Notification 생성
        return notificationBuilder.build()
    }
}