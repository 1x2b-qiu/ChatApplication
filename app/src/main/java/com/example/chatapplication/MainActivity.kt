package com.example.chatapplication // 应用主包名

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.chatapplication.ui.navigation.RootNavGraph
import com.example.chatapplication.ui.theme.ChatApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    // 定义通知权限注册器
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* 可按需处理结果 */ }

    // 声明状态并直接从 Intent 初始化
    private var pendingChat by mutableStateOf<Triple<String, String, String>?>(null)


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        pendingChat = intent.run {
            val id = getStringExtra("conversationId") ?: return@run null
            val name = getStringExtra("targetName") ?: return@run null
            val tid = getStringExtra("targetId") ?: return@run null
            //三元组返回
            Triple(id, name, tid)
        }


        // 💡 关键：在 setContent 之前，强制将窗口背景设为黑色
        window.setBackgroundDrawableResource(android.R.color.black)

        // 让系统启动页立即消失，不执行任何退出动画
        splashScreen.setOnExitAnimationListener { splashProvider ->
            splashProvider.remove()
        }

        // 设置系统栏透明并处理窗口边距
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 检查并请求权限
        checkNotificationPermission()

        setContent {
            ChatApplicationTheme {
                RootNavGraph(
                    pendingChat = pendingChat,
                    onPendingChat = { pendingChat = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingChat = intent.run {
            val id = getStringExtra("conversationId") ?: return@run null
            val name = getStringExtra("targetName") ?: return@run null
            val tid = getStringExtra("targetId") ?: return@run null
            //三元组返回
            Triple(id, name, tid)
        }

    }

    //检查并请求通知权限的规范封装
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 已有权限，无需操作
                }

                else -> {
                    // 启动请求
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

}
