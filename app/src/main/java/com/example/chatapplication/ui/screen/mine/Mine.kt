package com.example.chatapplication.ui.screen.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chatapplication.R
import com.example.chatapplication.data.model.Gender

@Composable
fun Mine(
    viewModel: MineViewModel = hiltViewModel(), onProfileClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    MineContent(uiState = uiState, onProfileClick = onProfileClick)

}


@Composable
fun MineContent(
    uiState: MineUiState, onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "我的",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_dehaze),
                    contentDescription = "菜单",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        MineCard(uiState = uiState, onProfileClick = onProfileClick)
        Spacer(modifier = Modifier.height(18.dp))
        PlayCard()
    }
}

@Composable
fun MineCard(
    uiState: MineUiState,
    onProfileClick: () -> Unit
) {
    Row( // 水平排列头像和文字内容
        modifier = Modifier
            .fillMaxWidth() // 占满宽度
            .clickable() { onProfileClick() },
        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            if (!uiState.contact?.avatar.isNullOrEmpty()) {
                AsyncImage(
                    model = uiState.contact?.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop, // 裁剪以填满方块
                    error = rememberVectorPainter(Icons.Default.AccountCircle),
                    fallback = rememberVectorPainter(Icons.Default.AccountCircle)
                )
            } else {
                // 没有头像时显示默认图标
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    tint = Color.White.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp)) // 头像与内容之间的间距

        Column(
            modifier = Modifier.wrapContentHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.contact?.name ?: "昵称",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (uiState.contact?.gender == Gender.MALE) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_male),
                        contentDescription = "男",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                } else if (uiState.contact?.gender == Gender.FEMALE) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_female),
                        contentDescription = "女",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "手机号：${uiState.contact?.phone}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier.offset(x = 12.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_right),
                contentDescription = "我的资料",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun PlayCard(
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
    ) {
        // 音乐
        PlayItem(
            icon = ImageVector.vectorResource(id = R.drawable.ic_music_note),
            label = "音乐",
            onClick = { /* 处理点击 */ }
        )
        // 相册
        PlayItem(
            icon = ImageVector.vectorResource(id = R.drawable.ic_collections),
            label = "相册",
            onClick = { /* 处理点击 */ }
        )
        // 表情
        PlayItem(
            icon = ImageVector.vectorResource(id = R.drawable.ic_emoji_emotions),
            label = "表情",
            onClick = { /* 处理点击 */ }
        )
        // 设置
        PlayItem(
            icon = Icons.Default.Settings,
            label = "设置",
            onClick = { /* 处理点击 */ }
        )
    }
}

@Composable
fun PlayItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row( // 水平排列头像和文字内容
        modifier = Modifier
            .fillMaxWidth() // 占满宽度
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(
                    width = 0.5.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(6.4.dp)
                )
                .clip(RoundedCornerShape(6.4.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_right),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
