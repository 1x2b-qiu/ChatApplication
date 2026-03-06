package com.example.chatapplication.ui.screen.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chatapplication.R
import com.example.chatapplication.data.model.Contact
import com.example.chatapplication.data.model.Gender
import com.example.chatapplication.ui.component.base.BaseTextField

@Composable
fun Contact(
    viewModel: ContactViewModel = hiltViewModel(),
    onContactClick: (String) -> Unit,
    onApplyClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearSearch()
    }

    ContactContent(uiState = uiState, onApplyClick = onApplyClick, onSearchQueryChange = {
        viewModel.onSearchQueryChange(it)
    }, onContactClick = onContactClick, onSearchClick = { viewModel.searchContacts() })
}


@Composable
fun ContactContent(
    uiState: ContactUiState,
    onApplyClick: () -> Unit,
    onSearchClick: () -> Unit,
    onContactClick: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    //管理键盘
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

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
                text = "联系人",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onApplyClick() }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        BaseTextField(
            value = uiState.searchQuery,
            onValueChange = { onSearchQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ),
            placeholderText = "搜索联系人（昵称或手机号）",
            cornerRadius = 12.dp,
            maxLines = 1,
            fontSize = 14,
            backgroundColor = Color.White.copy(alpha = 0.08f),
            textColor = Color.White,
            trailingIcon = {
                if (uiState.searchQuery.isNotBlank()) {
                    TextButton(
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onSearchClick()
                        }, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            "搜索",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)
                        )
                    }
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            })
        Spacer(modifier = Modifier.height(12.dp))


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (!uiState.searchResults.isNullOrEmpty()) {
                item {
                    Text(
                        text = "搜索结果", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp
                    )
                }
                items(uiState.searchResults) { contact ->
                    ContactItemView(contact = contact, onContactClick = onContactClick)
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
            if (uiState.hasSearched && uiState.searchResults == null && !uiState.isLoading) {
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
            item {
                Text(
                    text = "全部联系人", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp
                )
            }
            items(uiState.contacts) { contact ->
                ContactItemView(contact = contact, onContactClick = onContactClick)
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(start = 50.dp),
                    thickness = 0.5.dp,
                    color = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Preview
@Composable
fun ContactPreview() {
    ContactContent(
        uiState = ContactUiState(),
        onApplyClick = {},
        onSearchQueryChange = {},
        onContactClick = {},
        onSearchClick = {})
}

@Composable
fun ContactItemView(
    contact: Contact, onContactClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .clickable { onContactClick(contact.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.4.dp)
                )
        ) {
            AsyncImage(
                model = contact.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.4.dp)),
                contentScale = ContentScale.Crop,
                error = rememberVectorPainter(Icons.Default.AccountCircle),
                fallback = rememberVectorPainter(Icons.Default.AccountCircle)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = contact.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))

        if (contact.gender == Gender.MALE) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_male),
                contentDescription = "男",
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        } else if (contact.gender == Gender.FEMALE) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_female),
                contentDescription = "女",
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        }
    }
}
