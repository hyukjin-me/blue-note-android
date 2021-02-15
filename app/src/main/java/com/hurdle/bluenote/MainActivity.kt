package com.hurdle.bluenote

import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hurdle.bluenote.service.SheetHelperService
import com.hurdle.bluenote.utils.HelperConstants
import com.hurdle.bluenote.utils.HelperConstants.EXPANSION_VIEW
import com.hurdle.bluenote.utils.HelperConstants.ICON_VIEW
import com.hurdle.bluenote.utils.HelperConstants.INIT_NONE
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(setOf(), drawerLayout)

        bottomNavView = findViewById(R.id.main_bottom_nav)
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)

        // 포그라운드 서비스로 화면 재진입시 기존 서비스 종료
        if (intent.action == INIT_NONE) {
            val intent = Intent(this, SheetHelperService::class.java).apply {
                action = null
            }
            startService(intent)
        }

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            when (destination.id) {
                R.id.nav_home -> {
                    toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
                    toolbar.setNavigationOnClickListener {
                        // drawLayout 이벤트
                        drawerLayout.open()
                    }
                    // 하단뷰 보임
                    bottomNavView.visibility = View.VISIBLE
                }
                R.id.nav_note -> {
                    toolbar.setNavigationIcon(R.drawable.ic_baseline_note_24)
                    // drawLayout 열림, 또는 뒤로가기 이벤트 방지
                    toolbar.setNavigationOnClickListener {
                    }
                    // 하단뷰 보임
                    bottomNavView.visibility = View.VISIBLE
                }
                R.id.nav_note_create -> {
                    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
                    // 뒤로가기
                    toolbar.setNavigationOnClickListener {
                        controller.popBackStack()
                    }
                    // 하단뷰 보임
                    bottomNavView.visibility = View.VISIBLE
                }
                R.id.nav_note_page -> {
                    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
                    // 뒤로가기
                    toolbar.setNavigationOnClickListener {
                        controller.popBackStack()
                    }
                    // 하단뷰 숨김
                    bottomNavView.visibility = View.GONE
                }
                R.id.nav_sheet -> {
                    toolbar.setNavigationIcon(R.drawable.ic_baseline_ballot_24)
                    toolbar.setNavigationOnClickListener {
                    }
                    // 하단뷰 보임
                    bottomNavView.visibility = View.VISIBLE
                }
                R.id.nav_sheet_create -> {
                    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
                    // 뒤로가기
                    toolbar.setNavigationOnClickListener {
                        controller.popBackStack()
                    }
                    // 하단뷰 보임
                    bottomNavView.visibility = View.VISIBLE
                }
                R.id.nav_sheet_question -> {
                    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
                    // 뒤로가기
                    toolbar.setNavigationOnClickListener {
                        controller.popBackStack()
                    }
                    // 하단뷰 숨김
                    bottomNavView.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)

        // 모든 아이템 숨김
        menu.findItem(R.id.menu_edit).isVisible = false
        menu.findItem(R.id.menu_delete).isVisible = false
        menu.findItem(R.id.menu_search).isVisible = false
        return true
    }

    // 프레그먼트에서 툴바 타이틀 변경을 위한 메서드
    // https://hashcode.co.kr/questions/8676/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-fragment%EB%A1%9C-%EC%9D%B4%EB%8F%99%EC%8B%9C-settitle%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95
    fun setToolbarTitle(title: String) {
        toolbar.title = title
    }
}