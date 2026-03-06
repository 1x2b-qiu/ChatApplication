package com.example.chatapplication.ui.screen.chat

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chatapplication.data.model.Message
import com.example.chatapplication.ui.component.base.BaseTextField
import com.example.chatapplication.ui.component.base.BaseTopBar


@Composable
fun Chat(
    viewModel: ChatViewModel = hiltViewModel(), onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    ChatContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onValueChange = { viewModel.onInputTextChange(it) },
        onSenderClick = { viewModel.sendMessage() },
        onImageMessage = { viewModel.sendImageMessage(it) },
        listState = listState
    )

}

@Composable
fun ChatContent(
    uiState: ChatUiState,
    onBackClick: () -> Unit,
    onValueChange: (String) -> Unit,
    onSenderClick: () -> Unit,
    onImageMessage: (Uri) -> Unit,
    listState: LazyListState
) {

    // 选择图片的 launcher：拿到 Uri 后传给外部
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onImageMessage(it) }
    }

    // 权限 launcher：同意后立刻打开相册
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(), onResult = { isGranted ->
            if (isGranted) {
                // 权限已授予，打开相册
                pickImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        })

    // 根据系统版本选择要申请的权限
    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }


    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    LaunchedEffect(uiState.messages.size, imeHeight) {
        if (uiState.messages.isNotEmpty()) {
            listState.scrollToItem(uiState.messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            containerColor = Color.Transparent, topBar = {
                BaseTopBar(
                    title = uiState.targetName,
                    onBackClick = onBackClick,
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            }, contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding()
            ) {
                HorizontalDivider(
                    modifier = Modifier,
                    thickness = 0.5.dp,
                    color = Color.White.copy(alpha = 0.1f)
                )
                LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
                    items(uiState.messages) { message ->
                        ChatMessageItem(
                            message = message, senderAvatar = message.senderAvatar
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Transparent)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = {
                            permissionLauncher.launch(permissionToRequest)
                        }, modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    BaseTextField(
                        value = uiState.inputText,
                        onValueChange = { onValueChange(it) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 0.5.dp,
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        placeholderText = "输入消息...",
                        cornerRadius = 12.dp,
                        maxLines = 12,
                        backgroundColor = Color.White.copy(alpha = 0.08f),
                        textColor = Color.White,
                        verticalPadding = 4.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSenderClick() },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(6.dp),
                        enabled = uiState.inputText != "",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.White.copy(alpha = 0.3f),
                            disabledContentColor = Color.Black.copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        Text("发送")
                    }
                }
            }
        }
    }
}


@Composable
fun ChatMessageItem( // 单条聊天消息组件
    message: Message, // 消息数据
    senderAvatar: String?, // 发送方头像
    modifier: Modifier = Modifier // 样式修饰符
) {
    Row( // 水平排列头像和气泡
        modifier = modifier
            .fillMaxWidth() // 填满屏幕宽度
            .padding(horizontal = 8.dp, vertical = 4.dp), // 设置外边距
        horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top // 顶部对齐
    ) {
        if (message.isMe) {
            Spacer(modifier = Modifier.weight(1f))
        }
        if (!message.isMe) { // 如果是对方发送的，先显示头像
            if (!senderAvatar.isNullOrEmpty() && senderAvatar != "default_avatar") {
                AsyncImage(
                    model = senderAvatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.4.dp))
                        .border(
                            width = 0.5.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.4.dp)
                        ),
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.4.dp)),
                    tint = Color.LightGray
                )
            }
            Spacer(modifier = Modifier.width(8.dp)) // 头像与气泡的间距
        }

        Box( // 消息气泡容器
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip( // 裁剪气泡圆角
                    RoundedCornerShape(
                        topStart = 4.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp
                    )
                )
                .border(
                    width = 0.3.dp,
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(
                        topStart = 4.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp
                    )
                )
                .background(Color.White) // 我方绿色气泡，对方白色
                .padding(horizontal = 12.dp, vertical = 8.dp) // 气泡内部边距
        ) {
            Text( // 消息文本内容
                text = message.content, // 内容
                fontSize = 14.sp, // 字号
                fontWeight = FontWeight.Medium,
                color = Color.Black, // 字体颜色
                lineHeight = 22.sp, // 增加行高，阅读更舒适
                textAlign = TextAlign.Start
            )
        }

        if (message.isMe) { // 如果是我发送的，右侧显示头像
            Spacer(modifier = Modifier.width(8.dp)) // 气泡与头像的间距
            if (!senderAvatar.isNullOrEmpty() && senderAvatar != "default_avatar") {
                AsyncImage(
                    model = senderAvatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.4.dp))
                        .border(
                            width = 0.5.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.4.dp)
                        ),
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.4.dp)),
                    tint = Color.LightGray
                )
            }
        }
        if (!message.isMe) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
