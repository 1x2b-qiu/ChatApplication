package com.example.chatapplication.ui.screen.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import com.example.chatapplication.R

@Composable
fun Splash(
    viewModel: SplashViewModel = hiltViewModel(),
    onSuccess: () -> Unit,
    onFail: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    SplashContent(
        uiState = uiState,
        onSuccess = onSuccess,
        onFail = onFail
    )

}

@Composable
fun SplashContent(
    uiState: SplashUiState,
    onSuccess: () -> Unit,
    onFail: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoAnimation")

    //动画
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // 监听状态变化，执行跳转
    LaunchedEffect(uiState.isLoggedIn) {
        // 可加一点延迟，保证用户看到 Splash 动画
        delay(1000)
        if (uiState.isLoggedIn) {
            onSuccess()
        } else {
            onFail()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        alpha = alpha
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_rose),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    tint = Color.Black // 这里设置你想要的任何颜色
                )
            }
        }
    }
}

@Preview
@Composable
fun SplashPreview() {
    SplashContent(
        uiState = SplashUiState(),
        onFail = {},
        onSuccess = {}
    )
}