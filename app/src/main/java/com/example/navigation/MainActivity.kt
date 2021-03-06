package com.example.navigation

import android.net.Uri
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.navigation.util.NavUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        val hostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        NavUtil.builderNavGraph(
            this,
            hostFragment!!.childFragmentManager,
            navController,
            R.id.nav_host_fragment
        )
        NavUtil.builderBottomBar(navView)

        navView.setOnNavigationItemSelectedListener { item ->
            navController.navigate(item.itemId)
            true
        }
//        navView.setupWithNavController(navController)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home,
//                R.id.navigation_dashboard,
//                R.id.navigation_notifications,
//                R.id.navigation_user
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        navView.setupWithNavController(navController)

//        //跳转页面
//        navController.navigate(R.id.navigation_notifications)
//        navController.navigate(R.id.navigation_notifications,Bundle.EMPTY)
//        navController.navigate(Uri.parse("www.imooc.com"))
//
//        //页面回退
//        navController.navigateUp()
//        navController.popBackStack(R.id.navigation_dashboard,false)
    }
}