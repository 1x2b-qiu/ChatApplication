package com.example.chatapplication.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chatapplication.ui.screen.main.BottomNavItem
import com.example.chatapplication.ui.screen.contact.Contact
import com.example.chatapplication.ui.screen.conversation.Conversation
import com.example.chatapplication.ui.screen.conversation.ConversationViewModel
import com.example.chatapplication.ui.screen.mine.Mine

@Composable
fun Main(
    onNavigateToChat: (String, String, String) -> Unit,
    onNavigateToApply: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToContactProfile: (String) -> Unit,
    // 💡 注入 ConversationViewModel 以获取未读数
    conversationViewModel: ConversationViewModel = hiltViewModel()
) {
    //导航控制器
    val bottomNavController = rememberNavController()
    //返回栈
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    //当前导航
    val currentDestination = navBackStackEntry?.destination

    // 💡 观察会话列表，计算未读总数
    val convState by conversationViewModel.uiState.collectAsState()
    //算总数
    val totalUnreadCount = convState.conversations.sumOf { it.unreadCount }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                Surface(
                    modifier = Modifier.height(55.dp), // 稍微增加点高度以容纳红点空间
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavItem.items.forEach { item ->
                            val isSelected = currentDestination?.route == item.route

                            // 💡 使用 Box 包裹文字，方便在右上角加红点
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        bottomNavController.navigate(item.route) {
                                            popUpTo(bottomNavController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.label,
                                    fontSize = if (isSelected) 18.sp else 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = Color.White,
                                )

                                // 💡 如果是“消息”项且有未读消息，则显示红点
                                if (item == BottomNavItem.Conversations && totalUnreadCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .align(Alignment.TopEnd) // 对齐到右上角
                                            .offset(x = 6.dp, y = (-2).dp) // 微调位置，移出文字范围
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                    )
                                }
                            }
                        }
                    }
                }
            }) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = bottomNavController,
                    startDestination = BottomNavItem.Conversations.route,
                ) {
                    composable(BottomNavItem.Conversations.route) {
                        Conversation(
                            onConversationClick = onNavigateToChat
                        )
                    }
                    composable(BottomNavItem.Contacts.route) {
                        Contact(
                            onApplyClick = onNavigateToApply,
                            onContactClick = onNavigateToContactProfile
                        )
                    }
                    composable(BottomNavItem.Mine.route) {
                        Mine(onProfileClick = onNavigateToProfile)
                    }
                }
            }
        }
    }
}
