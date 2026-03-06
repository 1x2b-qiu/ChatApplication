package com.example.chatapplication.ui.screen.complete


import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chatapplication.data.model.Gender
import kotlinx.coroutines.launch
import com.example.chatapplication.R
import com.example.chatapplication.ui.component.loading.Loading

@Composable
fun Complete(
    viewModel: CompleteViewModel = hiltViewModel(), onCompleteSuccess: () -> Unit
) {
    //收集UI状态
    val uiState by viewModel.uiState.collectAsState()
    //监听是否提交成功
    LaunchedEffect(uiState.completeSuccess) {
        if (uiState.completeSuccess) onCompleteSuccess()
    }
    Box {
        CompleteContent(
            uiState = uiState,
            onNameChange = { viewModel.onNameChange(it) },
            onGenderChange = { viewModel.onGenderChange(it) },
            onAvatarChange = { viewModel.onAvatarChange(it) },
            onCompleteSuccess = { viewModel.complete() })
        Loading(isVisible = uiState.isLoading)
    }
}


@Composable
fun CompleteContent(
    uiState: CompleteUiState,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onAvatarChange: (String) -> Unit,
    onCompleteSuccess: () -> Unit
) {
    //分页状态
    val pagerState = rememberPagerState(pageCount = { 3 })
    //协程作用域
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .blur(if (uiState.isLoading) 8.dp else 0.dp), contentAlignment = Alignment.TopCenter
    ) {

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "背景",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 72.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(40.dp), // 大圆角
            color = Color.White.copy(alpha = 0.1f), // 极低的透明度模拟玻璃
            border = BorderStroke(
                width = 0.5.dp, color = Color.White.copy(alpha = 0.15f) // 细微的边框勾勒边缘
            ),
            // 注意：Android 原生对毛玻璃模糊的支持有限，主要靠透明度色彩叠加实现
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // 进度显示
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${pagerState.currentPage + 1}", style = TextStyle(
                            fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White
                        )
                    )
                    Text(
                        text = " / 3",
                        style = TextStyle(fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 进度条轨道
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .padding(horizontal = 24.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val indicatorWidth = maxWidth / 3
                        val scrollOffset =
                            indicatorWidth * (pagerState.currentPage + pagerState.currentPageOffsetFraction)
                        Box(
                            modifier = Modifier
                                .width(indicatorWidth)
                                .fillMaxHeight()
                                .offset(x = scrollOffset)
                                .background(Color.White, CircleShape)
                        )
                    }
                }

                //三个页面
                HorizontalPager(
                    state = pagerState, modifier = Modifier.weight(1f), userScrollEnabled = false
                ) { page ->
                    when (page) {
                        0 -> NameContent(onNext = {
                            coroutineScope.launch { pagerState.animateScrollToPage(1) }
                        }, uiState = uiState, onNameChange = {
                            onNameChange(it)
                        })

                        1 -> GenderContent(onNext = {
                            coroutineScope.launch { pagerState.animateScrollToPage(2) }
                        }, uiState = uiState, onGenderChange = {
                            onGenderChange(it)
                        })

                        2 -> AvatarContent(onFinish = {
                            onCompleteSuccess()
                        }, uiState = uiState, onAvatarChange = {
                            onAvatarChange(it)
                        })
                    }
                }
            }
        }
    }
}


@Composable
fun NameContent(
    onNext: () -> Unit, onNameChange: (String) -> Unit, uiState: CompleteUiState
) {
    var isButtonEnabled = uiState.name.isNotBlank()

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "我们要怎么称呼你？", style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "输入一个昵称，让你的朋友们认出你。", style = TextStyle(
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            ), modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = uiState.name,
            onValueChange = {
                if (it.length <= 6) {
                    onNameChange(it)
                }
            },
            placeholder = { Text("输入您的昵称", color = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            leadingIcon = {
                Icon(
                    Icons.Rounded.Face, contentDescription = null, tint = Color.White
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                focusedBorderColor = Color.White,
                cursorColor = Color.White
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = isButtonEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, disabledContainerColor = Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (isButtonEnabled) Color.White else Color.White.copy(alpha = 0.5f)
                    ), contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "下一步",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(64.dp))

    }
}

@Composable
fun GenderContent(
    onNext: () -> Unit, onGenderChange: (Gender) -> Unit, uiState: CompleteUiState
) {

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "选择你的性别", style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "这能帮我们为您提供更好的体验", style = TextStyle(
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            ), modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        GenderCard(
            title = "男",
            icon = ImageVector.vectorResource(R.drawable.ic_male),
            isSelected = uiState.gender == Gender.MALE,
            onClick = { onGenderChange(Gender.MALE) })
        GenderCard(
            title = "女",
            icon = ImageVector.vectorResource(R.drawable.ic_female),
            isSelected = uiState.gender == Gender.FEMALE,
            onClick = { onGenderChange(Gender.FEMALE) })
        GenderCard(
            title = "其他",
            icon = ImageVector.vectorResource(R.drawable.ic_transgender),
            isSelected = uiState.gender == Gender.UNKNOWN,
            onClick = { onGenderChange(Gender.UNKNOWN) })

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, disabledContainerColor = Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "下一步",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun AvatarContent(
    onFinish: () -> Unit, onAvatarChange: (String) -> Unit, uiState: CompleteUiState
) {

    // 选择图片的 launcher：拿到 Uri 后传给外部
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAvatarChange(it.toString()) }
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


    var isButtonEnabled = uiState.avatar.isNotBlank()

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "最后，上传一张头像", style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "真实的头像能让朋友更快认出你", style = TextStyle(
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            ), modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        // 2. 头像显示区域
        Box(
            contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(160.dp)
        ) {
            Surface(
                onClick = { permissionLauncher.launch(permissionToRequest) },
                modifier = Modifier.size(160.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.1f),
                border = BorderStroke(2.dp, Color.White.copy(alpha = 0.15f))
            ) {
                // 3. 根据 uiState.avatar 是否为空来切换显示内容
                if (uiState.avatar.isNotEmpty()) {
                    AsyncImage(
                        model = uiState.avatar,
                        contentDescription = "用户头像",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop // 裁剪以充满圆环
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxSize(),
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
            Surface(
                onClick = { permissionLauncher.launch(permissionToRequest) },
                modifier = Modifier
                    .size(44.dp)
                    .offset(x = (-4).dp, y = (-4).dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_camera),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = isButtonEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, disabledContainerColor = Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (isButtonEnabled) Color.White else Color.White.copy(alpha = 0.5f)
                    ), contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "完成",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}


@Composable
fun GenderCard(
    title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .height(72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
        border = if (isSelected) BorderStroke(
            1.dp, Color.White.copy(alpha = 0.5f)
        ) else BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                )
            }
        }
    }
}


// 1. 整体页面预览
@Preview(showBackground = true)
@Composable
fun CompletePreview() {
    val mockState = CompleteUiState(
        name = "张三", gender = Gender.MALE, avatar = ""
    )
    CompleteContent(
        uiState = mockState,
        onNameChange = {},
        onGenderChange = {},
        onAvatarChange = {},
        onCompleteSuccess = {})
}

// 2. 单独预览姓名输入页
@Preview(showBackground = true)
@Composable
fun NameContentPreview() {
    NameContent(
        onNext = {}, onNameChange = {}, uiState = CompleteUiState(name = "开发者")
    )
}

// 3. 单独预览性别选择页
@Preview(showBackground = true)
@Composable
fun GenderContentPreview() {
    GenderContent(
        onNext = {},
        onGenderChange = {},
        uiState = CompleteUiState(gender = Gender.FEMALE)
    )
}

// 4. 单独预览头像上传页
@Preview(showBackground = true)
@Composable
fun AvatarContentPreview() {
    AvatarContent(
        onFinish = {}, onAvatarChange = {}, uiState = CompleteUiState(avatar = "")
    )
}
