package com.example.chatapplication.ui.screen.profile.gender

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapplication.R
import com.example.chatapplication.data.model.Gender
import com.example.chatapplication.ui.component.loading.Loading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gender(
    onBack: () -> Unit, viewModel: GenderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Box {
        GenderContent(
            onBack = onBack,
            uiState = uiState,
            setGenderFemale = { viewModel.onGenderChange(Gender.FEMALE) },
            setGenderMale = { viewModel.onGenderChange(Gender.MALE) },
            updateGender = { viewModel.updateGender() })
        Loading(isVisible = uiState.isLoading)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderContent(
    uiState: GenderUiState,
    onBack: () -> Unit,
    updateGender: () -> Unit,
    setGenderMale: () -> Unit,
    setGenderFemale: () -> Unit
) {
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onBack()
    }

    //控制图标动态切换大小
    val maleIconSize by animateDpAsState(targetValue = if (uiState.gender == Gender.MALE) 100.dp else 80.dp)
    val femaleIconSize by animateDpAsState(targetValue = if (uiState.gender == Gender.FEMALE) 100.dp else 80.dp)

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
                            text = "修改性别",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Male Selection
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { setGenderMale() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_male),
                            contentDescription = "男",
                            modifier = Modifier.size(maleIconSize),
                            tint = if (uiState.gender == Gender.MALE) Color.White else Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Female Selection
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { setGenderFemale() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_female),
                            contentDescription = "女",
                            modifier = Modifier.size(femaleIconSize),
                            tint = if (uiState.gender == Gender.FEMALE) Color.White else Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = { updateGender() },
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
fun GenderPreview() {
    GenderContent(
        onBack = {},
        updateGender = {},
        uiState = GenderUiState(),
        setGenderMale = {},
        setGenderFemale = {})
}
