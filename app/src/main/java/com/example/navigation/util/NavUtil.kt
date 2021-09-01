package com.example.navigation.util

import android.content.ComponentName
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.DialogFragmentNavigator
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.example.navigation.R
import com.example.navigation.model.BottomBar
import com.example.navigation.model.Destination
import com.example.navigation.widget.HiFragmentNavigator
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * ================================================
 * 类名：com.example.navigation.util
 * 时间：2021/8/31 16:20
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
object NavUtil {

    lateinit var destinations: HashMap<String, Destination>

    private fun parseFile(context: Context, fileName: String): String {
        val assets = context.assets
        val inputStream = assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        val builder = StringBuilder()
        while ((bufferedReader.readLine().also { line = it }) != null) {
            builder.append(line)
        }

        inputStream.close()
        bufferedReader.close()
        return builder.toString()
    }

    fun builderNavGraph(
        activity: FragmentActivity,
        childFragmentManager: FragmentManager,
        controller: NavController,
        containerId: Int
    ) {
        val content = parseFile(activity, "destination.json")
        destinations = JSON.parseObject(
            content,
            object : TypeReference<HashMap<String?, Destination?>?>() {}.type
        )


        val iterator = destinations.values.iterator()
        val provider = controller.navigatorProvider


        val graphNavigator = provider.getNavigator(NavGraphNavigator::class.java)
        val navGraph = NavGraph(graphNavigator)

        val hiFragmentNavigator =
            HiFragmentNavigator(activity, childFragmentManager, containerId)
        provider.addNavigator(hiFragmentNavigator)
        while (iterator.hasNext()) {
            val destination = iterator.next()
            when (destination.destType) {
                "activity" -> {
                    val navigator = provider.getNavigator(ActivityNavigator::class.java)
                    val node = navigator.createDestination()
                    node.id = destination.id
                    node.setComponentName(ComponentName(activity.packageName, destination.clazName))
                    navGraph.addDestination(node)
                }

                "fragment" -> {
//                    val navigator = provider.getNavigator(FragmentNavigator::class.java)
//                    val node = navigator.createDestination()
                    val node = hiFragmentNavigator.createDestination()
                    node.id = destination.id
                    node.className = destination.clazName
                    navGraph.addDestination(node)
                }

                "dialog" -> {
                    val navigator = provider.getNavigator(DialogFragmentNavigator::class.java)
                    val node = navigator.createDestination()
                    node.id = destination.id
                    node.className = destination.clazName
                    navGraph.addDestination(node)
                }
            }

            if (destination.asStarter) {
                navGraph.startDestination = destination.id
            }
        }
        controller.graph = navGraph
    }


    fun builderBottomBar(navView: BottomNavigationView) {
        val content = parseFile(navView.context, "main_tabs_config.json")
        val bottomBar = JSON.parseObject(content, BottomBar::class.java)
        val tabs = bottomBar.tabs
        val menu = navView.menu
        for (tab in tabs) {
            if (!tab.enable) continue
            val destination = destinations[tab.pageUrl]
            if (destination != null) {
                val menuItem = menu.add(0, destination.id, tab.index, tab.title)
                //可以根据title或者别的处理一下icon
                menuItem.setIcon(getIconDrawable(tab.title))
            }
        }
    }

    private fun getIconDrawable(title: String): Int {
        when (title) {
            "首页" -> return R.drawable.ic_home_black_24dp
            "菜单" -> return R.drawable.ic_dashboard_black_24dp
            "通知" -> return R.drawable.ic_notifications_black_24dp
            "我的" -> return R.drawable.ic_user_black_24dp
        }
        return R.drawable.ic_home_black_24dp
    }

}