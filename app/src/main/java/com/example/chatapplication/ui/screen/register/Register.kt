package com.example.chatapplication.ui.screen.register

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.chatapplication.R
import com.example.chatapplication.ui.component.base.BaseTextField
import com.example.chatapplication.ui.component.loading.Loading
import com.example.chatapplication.viewmodel.RegisterViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box {
        RegisterContent(
            uiState = uiState,
            onBack = onBack,
            onRegisterSuccess = onRegisterSuccess,
            onRegisterClick = { viewModel.register() },
            onPhoneChange = { viewModel.onPhoneChange(it) },
            onPasswordHashChange = { viewModel.onPasswordChange(it) },
            onConfirmPasswordHashChange = { viewModel.onConfirmPasswordChange(it) })
        Loading(isVisible = uiState.isLoading)
    }
}


@Composable
fun RegisterContent(
    uiState: RegisterUiState,
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordHashChange: (String) -> Unit,
    onConfirmPasswordHashChange: (String) -> Unit
) {
    //密码可见性
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    //按钮可用状态
    val isButtonEnabled =
        uiState.phone.isNotBlank() && uiState.passwordHash.isNotBlank() && uiState.passwordHash == uiState.confirmPasswordHash

    // 根据错误文案判断是手机号还是密码相关错误
    val phoneError = uiState.error?.contains("手机号") == true
    val passwordError = uiState.error?.contains("密码") == true

    //开始监听是否注册成功
    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            onRegisterSuccess()
        }
    }

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
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "注册新账号",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "与我们一起开启您的旅程",
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
                                color = if (phoneError) Color(0xFFFF5252) else Color.White.copy(
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
                                ImageVector.vectorResource(id = R.drawable.ic_phone_android),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        })
                }
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "密码",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BaseTextField(
                        value = uiState.passwordHash,
                        onValueChange = { onPasswordHashChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(
                                width = 0.5.dp,
                                color = if (passwordError) Color(0xFFFF5252) else Color.White.copy(
                                    alpha = 0.5f
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        placeholderText = "请输入密码（至少六位）",
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
                                        R.drawable.ic_visibility_off
                                    ), contentDescription = null, tint = Color.White
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "确认密码",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BaseTextField(
                        value = uiState.confirmPasswordHash,
                        onValueChange = { onConfirmPasswordHashChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(
                                width = 0.5.dp,
                                color = if (passwordError) Color(0xFFFF5252) else Color.White.copy(
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
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_verified_user),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) ImageVector.vectorResource(
                                        id = R.drawable.ic_visibility
                                    ) else ImageVector.vectorResource(
                                        R.drawable.ic_visibility_off
                                    ), contentDescription = null, tint = Color.White
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // 错误提示条
                if (uiState.error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0x33FF5252), shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "", color = Color(0xFFFFCDD2), fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Button(
                    onClick = { onRegisterClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    enabled = isButtonEnabled,
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = if (isButtonEnabled) Color.White else Color.White.copy(alpha = 0.5f)
                            ), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "注册",
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
                    Text("已经有账户?", color = Color.White, fontSize = 14.sp)
                    TextButton(onClick = { onBack() }) {
                        Text(
                            "登录",
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

@Preview
@Composable
fun RegisterPreview() {
    val mockState = RegisterUiState()
    RegisterContent(
        uiState = mockState,
        onBack = {},
        onPhoneChange = {},
        onPasswordHashChange = {},
        onRegisterClick = {},
        onConfirmPasswordHashChange = {},
        onRegisterSuccess = {})
}
