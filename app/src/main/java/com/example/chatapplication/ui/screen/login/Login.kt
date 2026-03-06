package com.example.chatapplication.ui.screen.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapplication.ui.component.base.BaseTextField
import com.example.chatapplication.R
import com.example.chatapplication.ui.component.loading.Loading

@Composable
fun Login(
    viewModel: LoginViewModel = hiltViewModel(),
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box {
        LoginContent(
            uiState = uiState,
            onPhoneChange = { viewModel.onPhoneChange(it) },
            onPasswordHashChange = { viewModel.onPasswordHashChange(it) },
            onLoginClick = { viewModel.login() },
            onRegisterClick = onRegisterClick,
            onLoginSuccess = onLoginSuccess
        )
        Loading(isVisible = uiState.isLoading)
    }

}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onPhoneChange: (String) -> Unit,
    onPasswordHashChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    //密码可见性
    var passwordVisible by remember { mutableStateOf(false) }
    //按钮是否可用
    var isButtonEnabled = uiState.phone.isNotBlank() && uiState.passwordHash.isNotBlank()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .blur(if (uiState.isLoading) 8.dp else 0.dp),
        contentAlignment = Alignment.TopCenter
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_rose), // 这是一个类似的星星/闪烁图标
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color.Black // 这里设置你想要的任何颜色
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "欢迎",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "登录以继续您的流程",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "手机号",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BaseTextField(
                        value = uiState.phone,
                        onValueChange = { onPhoneChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(
                                width = 0.5.dp,
                                color = if (uiState.error != null) Color(0xFFFF5252) else Color.White.copy(
                                    alpha = 0.5f
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        placeholderText = "请输入手机号",
                        cornerRadius = 12.dp,
                        maxLines = 1,
                        fontSize = 14,
                        backgroundColor = Color.White.copy(alpha = 0.08f),
                        textColor = Color.White,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_phone_android), // 使用手机图标更贴切
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        })
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "密码",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(
                            onClick = {},
                            // 关键：将 contentPadding 设为 0，防止它撑开高度
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.heightIn(min = 1.dp) // 允许它缩小
                        ) {
                            Text("忘记密码?", color = Color.White, fontSize = 14.sp)
                        }
                    }
                    BaseTextField(
                        value = uiState.passwordHash,
                        onValueChange = { onPasswordHashChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(
                                width = 0.5.dp,
                                color = if (uiState.error != null) Color(0xFFFF5252) else Color.White.copy(
                                    alpha = 0.5f
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        placeholderText = "请输入密码",
                        cornerRadius = 12.dp,
                        maxLines = 1,
                        fontSize = 14,
                        backgroundColor = Color.White.copy(alpha = 0.08f),
                        textColor = Color.White,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) ImageVector.vectorResource(id = R.drawable.ic_visibility) else ImageVector.vectorResource(
                                        id = R.drawable.ic_visibility_off
                                    ), contentDescription = null, tint = Color.White
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // 错误提示条
                if (uiState.error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0x33FF5252),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "",
                            color = Color(0xFFFFCDD2),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Button(
                    onClick = { onLoginClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = isButtonEnabled,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = if (isButtonEnabled) Color.White else Color.White.copy(alpha = 0.5f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "登录",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.3f)
                    )
                    Text("微聊", color = Color.White, fontSize = 14.sp)
                    HorizontalDivider(
                        modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.3f)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("没有账号?", color = Color.White, fontSize = 14.sp)
                    TextButton(onClick = onRegisterClick) {
                        Text(
                            "注册",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}


@Preview()
@Composable
fun LoginPreview() {
    val mockState = LoginUiState()
    LoginContent(
        uiState = mockState,
        onLoginSuccess = {},
        onRegisterClick = {},
        onLoginClick = {},
        onPasswordHashChange = {},
        onPhoneChange = {})
}