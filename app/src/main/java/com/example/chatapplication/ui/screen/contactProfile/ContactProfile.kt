package com.example.chatapplication.ui.screen.contactProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatapplication.R
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chatapplication.data.model.Gender


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactProfile(
    viewModel: ContactProfileViewModel = hiltViewModel(),
    onConversationClick: (String, String, String) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onConversationClick = {
            viewModel.createConversation()
            onConversationClick(uiState.contact!!.id, uiState.contact!!.name, uiState.contact!!.id)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    uiState: ContactProfileUiState,
    onConversationClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            containerColor = Color.Transparent, modifier = Modifier.fillMaxSize(), topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "好友信息",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                                contentDescription = "返回",
                                modifier = Modifier.size(28.dp),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(19.2.dp))
                ) {
                    if (uiState.contact?.avatar != null) {
                        AsyncImage(
                            model = uiState.contact.avatar,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(19.2.dp))
                                .border(
                                    width = 0.5.dp,
                                    color = Color.White.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(19.2.dp)
                                ),
                            contentScale = ContentScale.Crop, // 裁剪以填满方块
                            error = rememberVectorPainter(Icons.Default.AccountCircle),
                            fallback = rememberVectorPainter(Icons.Default.AccountCircle)
                        )
                    } else {
                        // 没有头像时显示默认图标
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "头像",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(36.dp)),
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = uiState.contact?.name ?: "昵称",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(36.dp))
                InfoRowWithIcon(
                    icon = ImageVector.vectorResource(id = R.drawable.ic_phone_android),
                    label = "手机号",
                    value = uiState.contact?.phone ?: "未绑定"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    thickness = 0.5.dp,
                    color = Color.White.copy(alpha = 0.3f)
                )
                InfoRowWithIcon(
                    icon = Icons.Default.Person,
                    label = "性别",
                    value = when (uiState.contact?.gender) {
                        Gender.MALE -> "男"
                        Gender.FEMALE -> "女"
                        else -> "未设置"
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    thickness = 0.5.dp,
                    color = Color.White.copy(alpha = 0.3f)
                )
                InfoRowWithIcon(
                    icon = Icons.Default.LocationOn,
                    label = "地区",
                    value = "上海"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    thickness = 0.5.dp,
                    color = Color.White.copy(alpha = 0.3f)
                )
                InfoRowWithIcon(
                    icon = Icons.Default.Email,
                    label = "邮箱",
                    value = "design@flow.com"
                )
                Spacer(modifier = Modifier.height(24.dp))
                if (uiState.isMyProfile == false) {
                    Button(
                        onClick = {
                            onConversationClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "发消息",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRowWithIcon(
    icon: ImageVector, label: String, value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 标签文字
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        // 右侧内容文字
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.End
        )
    }
}
