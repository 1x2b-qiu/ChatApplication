package com.example.chatapplication.ui.screen.profile.name

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapplication.ui.component.base.BaseTextField
import com.example.chatapplication.R
import com.example.chatapplication.ui.component.loading.Loading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Name(
    onBack: () -> Unit, viewModel: NameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box {
        NameContent(
            onNameChange = { viewModel.onNameChange(it) },
            onUpdateClick = { viewModel.updateName() },
            onBack = onBack,
            uiState = uiState
        )
        Loading(isVisible = uiState.isLoading)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameContent(
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onUpdateClick: () -> Unit,
    uiState: NameUiState
) {
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .blur(if (uiState.isLoading) 8.dp else 0.dp)
    ) {
        Scaffold(
            containerColor = Color.Transparent, modifier = Modifier.fillMaxSize(), topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "修改昵称",
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
                BaseTextField(
                    value = uiState.name,
                    onValueChange = {
                        if (it.length <= 6) {
                            onNameChange(it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(
                            width = 0.5.dp,
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    placeholderText = "请输入新的昵称",
                    cornerRadius = 12.dp,
                    maxLines = 1,
                    fontSize = 14,
                    backgroundColor = Color.White.copy(alpha = 0.08f),
                    textColor = Color.White
                )
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = { onUpdateClick() },
                    modifier = Modifier
                        .width(120.dp)
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
                            "确认",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NamePreview() {
    NameContent(
        onBack = {}, onNameChange = {}, onUpdateClick = {}, uiState = NameUiState()
    )
}
