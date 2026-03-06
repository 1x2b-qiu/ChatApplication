package com.example.chatapplication.ui.component.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import com.example.chatapplication.R
import androidx.compose.material3.CenterAlignedTopAppBar // 使用居中对齐的标题栏
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTopBar(
    title: String, // 标题文字
    modifier: Modifier = Modifier, // 样式修饰符
    showBackButton: Boolean = true, // 是否显示返回按钮
    showButton: Boolean = false,
    onBackClick: () -> Unit = {}, // 返回按钮点击事件
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}, // 右侧动作按钮槽位
    containerColor: Color = Color.White, // 背景颜色
    contentColor: Color = Color.Black // 文字和图标颜色
) {
    // 使用 CenterAlignedTopAppBar 实现标题自动居中
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium, // 微信标题通常较粗
                color = contentColor
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "返回",
                        Modifier.size(28.dp),
                        tint = contentColor
                    )
                }
            } else if (showButton) {
                navigationIcon()
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors( // 注意这里也要对应修改
            containerColor = containerColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor
        )
    )
}