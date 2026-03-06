package com.example.chatapplication.ui.screen.apply

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chatapplication.data.model.Contact
import com.example.chatapplication.data.model.ContactApply
import com.example.chatapplication.ui.component.base.BaseTextField
import com.example.chatapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Apply(
    viewModel: ApplyViewModel = hiltViewModel(), onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    //控制搜索结果是否可见
    val isPopupVisible = uiState.hasSearched && uiState.searchResult != null

    //管理键盘
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isPopupVisible) 15.dp else 0.dp),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "添加好友",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onBackClick() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
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
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                BaseTextField(
                    value = uiState.searchQuery,
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            TextButton(
                                onClick = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    viewModel.searchContact()
                                },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    "搜索",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)
                                )
                            }
                        }
                    },
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(
                            width = 0.5.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    placeholderText = "通过手机号搜索",
                    cornerRadius = 12.dp,
                    maxLines = 1,
                    fontSize = 14,
                    backgroundColor = Color.White.copy(alpha = 0.08f),
                    textColor = Color.White,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    })
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (uiState.hasSearched && uiState.searchResult == null && !uiState.isLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "未搜索到该联系人",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                    if (uiState.pendingApplys.isNotEmpty()) {
                        item {
                            Text(
                                "待处理",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(uiState.pendingApplys) { request ->
                            PendingCard(request = request, onAccept = { applyId, action ->
                                viewModel.handleContact(applyId, action)
                            }, onDecline = { applyId, action ->
                                viewModel.handleContact(applyId, action)
                            })
                        }
                    }
                    if (uiState.sentApplys.isNotEmpty()) {
                        item {
                            Text(
                                "申请中",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(uiState.sentApplys) { contactApply ->
                            SentCard(contactApply = contactApply)
                        }
                    }

                    if (uiState.recentActivity.isNotEmpty()) {
                        item {
                            Text(
                                "最近动态",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(uiState.recentActivity) { activity ->
                            RecentActivityItem(activity = activity)
                        }
                    }
                }
            }
        }
        if (isPopupVisible) {
            SearchResultCard(
                contact = uiState.searchResult,
                hasSearched = uiState.hasSearched,
                onApply = { viewModel.applyContact() },
                onDismiss = { viewModel.clearSearchResult() })
        }
    }
}


@Composable
fun SearchResultCard(
    contact: Contact?, hasSearched: Boolean, onApply: () -> Unit, onDismiss: () -> Unit
) {
    if (!hasSearched || contact == null) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null
            ) { onDismiss() }, contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1E1E1E),
            border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .clickable(enabled = false) {}) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                AsyncImage(
                    model = contact?.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 0.5.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(Icons.Default.AccountCircle),
                    fallback = rememberVectorPainter(Icons.Default.AccountCircle)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = contact?.name ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = contact?.phone ?: "",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                val isMale = contact?.gender?.name == "MALE"
                val genderBg = Color.White.copy(alpha = 0.1f)
                val genderColor = Color.White

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(genderBg)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isMale) "♂ 男" else "♀ 女",
                        color = genderColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .clickable {
                            onApply()
                            onDismiss()
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "申请添加为好友",
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


@Composable
fun PendingCard(
    request: ContactApply, onAccept: (Int, String) -> Unit, onDecline: (Int, String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = request.contact.avatar,
                    contentDescription = "头像",
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
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(request.contact.name, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        "申请添加你为好友", fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { onDecline(request.applyId, "reject") }) {
                    Text("拒绝", color = Color.White.copy(alpha = 0.5f))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onAccept(request.applyId, "accept") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text("同意", color = Color.Black, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun SentCard(contactApply: ContactApply) {
    Surface(
        modifier = Modifier.fillMaxWidth(), color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = contactApply.contact.avatar,
                contentDescription = "头像",
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
            Spacer(Modifier.width(12.dp))
            Text(
                contactApply.contact.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(Modifier.weight(1f))
            Text(
                "申请中", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp
            )
        }
    }
}

@Composable
fun RecentActivityItem(activity: ContactApply) {
    Surface(
        modifier = Modifier.fillMaxWidth(), color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = activity.contact.avatar,
                contentDescription = "头像",
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
            Spacer(Modifier.width(12.dp))
            Text(
                activity.contact.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(Modifier.weight(1f))
            val text = when {
                activity.status == "accepted" && activity.isMySent == true -> "对方已同意"
                activity.status == "accepted" && activity.isMySent == false -> "已同意"
                activity.status == "rejected" && activity.isMySent == true -> "对方已拒绝"
                else -> "已拒绝"
            }
            Text(
                text, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp
            )
        }
    }
}
