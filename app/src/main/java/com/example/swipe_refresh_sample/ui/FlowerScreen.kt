package com.example.swipe_refresh_sample.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swipe_refresh_sample.R
import com.example.swipe_refresh_sample.ui.theme.SwiperefreshsampleTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun FlowerScreen(viewModel: FlowerViewModel = viewModel()) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.refresh() },
        indicator = { s, trigger ->
            SunnySwipeRefreshIndicator(
                state = s,
                refreshTriggerDistance = trigger
            )
        }
    ) {
        FlowerContent()
    }
}

@Composable
private fun FlowerContent() {
    BoxWithConstraints {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Column(
                    modifier = Modifier.height(maxHeight * 0.7f),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_flower),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(100.dp)
                    )
                }
            }
            items(groundColors) { color ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    color = color
                ) {}
            }
        }
    }
}

private val groundColors = listOf(
    Color(0xFFEFEBE9), // Brown 50
    Color(0xFFD7CCC8), // Brown 100
    Color(0xFFBCAAA4), // Brown 200
    Color(0xFFA1887F), // Brown 300
    Color(0xFF8D6E63), // Brown 400
    Color(0xFF795548), // Brown 500
    Color(0xFF6D4C41), // Brown 600
    Color(0xFF5D4037), // Brown 700
    Color(0xFF4E342E), // Brown 800
    Color(0xFF3E2723), // Brown 900
)

@Preview
@Composable
fun FlowerScreenPreview() {
    SwiperefreshsampleTheme {
        Surface {
            FlowerScreen()
        }
    }
}