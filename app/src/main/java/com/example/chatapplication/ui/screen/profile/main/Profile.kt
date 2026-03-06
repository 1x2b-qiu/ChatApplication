package com.example.chatapplication.ui.screen.profile.main

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chatapplication.data.model.Gender
import com.example.chatapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    onBack: () -> Unit,
    onName: () -> Unit,
    onGender: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileContent(
        onBack = onBack,
        onName = onName,
        onGender = onGender,
        onAvatarClick = { viewModel.updateAvatar(it) },
        uiState = uiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    onBack: () -> Unit,
    onName: () -> Unit,
    onGender: () -> Unit,
    onAvatarClick: (String) -> Unit,
    uiState: ProfileUiState
) {

    // 选择图片的 launcher：拿到 Uri 后传给外部
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAvatarClick(it.toString()) }
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
                            text = "个人信息",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                ImageVector.vectorResource(id = R.drawable.ic_back),
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
                        .clickable(onClick = { permissionLauncher.launch(permissionToRequest) })
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
                TextButton(
                    onClick = { onName() }) {
                    Text(
                        text = uiState.contact?.name ?: "昵称",
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(36.dp))
                InfoRowWithIcon(
                    icon = ImageVector.vectorResource(id = R.drawable.ic_phone_android),
                    label = "手机号",
                    value = uiState.contact?.phone ?: "未绑定",
                    onClick = {})
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
                    },
                    onClick = { onGender() })
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    thickness = 0.5.dp,
                    color = Color.White.copy(alpha = 0.3f)
                )
                InfoRowWithIcon(
                    icon = Icons.Default.LocationOn,
                    label = "地区",
                    value = "上海",
                    onClick = {})
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    thickness = 0.5.dp,
                    color = Color.White.copy(alpha = 0.3f)
                )
                InfoRowWithIcon(
                    icon = Icons.Default.Email,
                    label = "邮箱",
                    value = "design@flow.com",
                    onClick = {})
            }
        }
    }
}

@Composable
fun InfoRowWithIcon(
    icon: ImageVector, label: String, value: String, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun ProfilePreview() {
    ProfileContent(
        onGender = {},
        onName = {},
        onBack = {},
        onAvatarClick = {},
        uiState = ProfileUiState()
    )
}
