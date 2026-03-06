package com.example.chatapplication.ui.screen.conversation

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chatapplication.R
import com.example.chatapplication.data.model.Conversation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun Conversation(
    viewModel: ConversationViewModel = hiltViewModel(),
    onConversationClick: (String, String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    //是否展示删除警告框
    var showDeleteDialog by remember { mutableStateOf(false) }
    //传递conversation
    var conversationToDelete by remember { mutableStateOf<Conversation?>(null) }

    ConversationContent(
        uiState = uiState,
        onConversationClick = onConversationClick,
        onTogglePinned = { conversation ->
            viewModel.toggleConversationIsPinned(conversation)
        },
        onDeleteConversation = { conversation ->
            conversationToDelete = conversation
            showDeleteDialog = true
        })

    if (showDeleteDialog && conversationToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                conversationToDelete = null
            },
            modifier = Modifier.border(
                BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp)
            ),
            shape = RoundedCornerShape(12.dp),
            containerColor = Color(0xFF1E1E1E),
            title = { Text("确认删除", fontSize = 18.sp, color = Color.White) },
            text = {
                Text(
                    "确定要删除和“${conversationToDelete!!.contact.name}”的会话吗？",
                    color = Color.White.copy(alpha = 0.5f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteConversation(conversationToDelete!!.id)
                        showDeleteDialog = false
                        conversationToDelete = null
                    }) {
                    Text("删除", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        conversationToDelete = null
                    }) {
                    Text("取消", color = Color.White)
                }
            })
    }

}

@Composable
fun ConversationContent(
    uiState: ConversationUiState,
    onConversationClick: (String, String, String) -> Unit,
    onDeleteConversation: (Conversation) -> Unit,
    onTogglePinned: (Conversation) -> Unit
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
                text = "聊天", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (uiState.conversations.isEmpty() && !uiState.isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "主页",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.4.dp))
                        .border(
                            width = 0.5.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.4.dp)
                        ),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "小仓鼠",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = formatTime(System.currentTimeMillis()),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )

                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "快去添加好友聊天吧！\uD83D\uDE18",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.conversations) { conversation ->
                    var expanded by remember { mutableStateOf(false) }
                    var pressOffest by remember { mutableStateOf(Offset.Zero) }
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ConversationItem(conversation = conversation, onClick = {
                            onConversationClick(
                                conversation.id, conversation.contact.name, conversation.contact.id
                            )
                        }, onLongPress = {
                            pressOffest = it
                            expanded = true
                        }

                        )
                        if (expanded) {
                            val density = LocalDensity.current

                            // ★ 新增：定义菜单宽度
                            val popupWidth = 100.dp

                            // ★ 新增：根据点击位置和菜单宽度计算偏移，让右边缘对齐手指
                            val popupOffset = with(density) {
                                IntOffset(
                                    (pressOffest.x - popupWidth.toPx()).toInt(),
                                    pressOffest.y.toInt()
                                )
                            }
                            Popup(
                                alignment = Alignment.TopStart,
                                offset = popupOffset,
                                onDismissRequest = { expanded = false }) {
                                Column(
                                    modifier = Modifier
                                        .width(popupWidth)
                                        .background(Color(0xFF1E1E1E), RoundedCornerShape(6.dp))
                                        .border(
                                            0.5.dp,
                                            Color.White.copy(alpha = 0.15f),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (conversation.isPinned) "取消置顶" else "置顶会话",
                                        color = Color.White,
                                        modifier = Modifier
                                            .clickable {
                                                expanded = false
                                                onTogglePinned(conversation)
                                            }
                                            .padding(horizontal = 16.dp, vertical = 12.dp))


                                    Text(
                                        "删除会话",
                                        color = Color.Red,
                                        modifier = Modifier
                                            .clickable {
                                                expanded = false
                                                onDeleteConversation(conversation)
                                            }
                                            .padding(horizontal = 16.dp, vertical = 12.dp))
                                }
                            }
                        }

                    }
                    HorizontalDivider(
                        modifier = Modifier,
                        thickness = 0.5.dp,
                        color = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ConversationItem(
    conversation: Conversation, onClick: () -> Unit, onLongPress: (Offset) -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredWidth(LocalConfiguration.current.screenWidthDp.dp - 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (conversation.isPinned) Color(0xFF1E1E1E) else Color.Transparent)
            .indication(
                interactionSource = interactionSource, indication = ripple(color = Color.White)
            )
            //给当前 Composable 增加一个基于指针（手势）的输入处理
            .pointerInput(Unit) {
                //使用 Compose 提供的 detectTapGestures 手势检测器，集中处理点按相关的手势（点击、长按、双击等
                detectTapGestures(onPress = { offset ->
                    val press = PressInteraction.Press(offset)
                    interactionSource.emit(press)
                    val released = tryAwaitRelease()
                    if (released) {
                        //如果是「正常抬起」，向 interactionSource 发送一个 Release 事件，结束按压动画 / 反馈
                        interactionSource.emit(PressInteraction.Release(press))
                    } else {
                        //如果不是正常抬起（被取消了），发送一个 Cancel 事件，让波纹 / 按压状态被立即取消
                        interactionSource.emit(PressInteraction.Cancel(press))
                    }
                }, onTap = { onClick() }, onLongPress = { offset ->
                    onLongPress(offset)
                })
            }
            .padding(vertical = 12.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp)
        ) {
            AsyncImage(
                model = conversation.contact.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.4.dp))
                    .border(
                        width = 0.5.dp,
                        color = Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.4.dp)
                    ),
                contentScale = ContentScale.Crop,
                error = rememberVectorPainter(Icons.Default.AccountCircle),
                fallback = rememberVectorPainter(Icons.Default.AccountCircle)
            )

            if (conversation.unreadCount > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                        .border(0.5.dp, Color.Black, CircleShape),
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    Text(
                        text = if (conversation.unreadCount > 99) "99+" else conversation.unreadCount.toString(),
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.contact.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                if (conversation.lastMessageTime != null) {
                    Text(
                        text = formatTime(conversation.lastMessageTime),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = conversation.lastMessage ?: "",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    val nowCalendar = Calendar.getInstance()

    return when {
        diff < 60000 -> "刚刚"
        calendar.get(Calendar.DATE) == nowCalendar.get(Calendar.DATE) -> {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        }

        calendar.get(Calendar.DATE) == nowCalendar.get(Calendar.DATE) - 1 -> "昨天"
        else -> SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(timestamp))
    }
}
