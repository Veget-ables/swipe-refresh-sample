package com.example.swipe_refresh_sample.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.swipe_refresh_sample.R
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@Composable
fun SunnySwipeRefreshIndicator(
    state: SwipeRefreshState,
    refreshTriggerDistance: Dp,
    modifier: Modifier = Modifier,
) {
    val indicatorRefreshTrigger = with(LocalDensity.current) { refreshTriggerDistance.toPx() }
    val indicatorHeight = with(LocalDensity.current) { indicatorSize.roundToPx() }
    val refreshingOffsetPx = with(LocalDensity.current) { 16.dp.toPx() }

    val slingshotOffset by rememberUpdatedSlingshotOffset(
        offsetY = state.indicatorOffset,
        maxOffsetY = indicatorRefreshTrigger,
        height = indicatorHeight,
    )

    // ユーザのスワイプ操作中、操作後のIndicatorのy方向の位置を表すオフセット
    var offset by remember { mutableStateOf(0f) }

    if (state.isSwipeInProgress) {
        // 手動でスワイプしているときのIndicatorの位置
        offset = slingshotOffset.toFloat()
    } else {
        // スワイプしてユーザの手が離れた後のIndicatorの位置をアニメーションする
        LaunchedEffect(state.isRefreshing) {
            animate(
                initialValue = offset,
                targetValue = when {
                    state.isRefreshing -> indicatorHeight + refreshingOffsetPx
                    else -> 0f
                }
            ) { value, _ ->
                offset = value
            }
        }
    }

    val adjustedElevation = when {
        state.isRefreshing -> 6.dp
        offset > 0.5f -> 6.dp
        else -> 0.dp
    }

    // Indicator本体
    Surface(
        modifier = modifier
            .size(size = indicatorSize)
            .graphicsLayer {
                translationY = offset - indicatorHeight

                val scaleFraction = if (!state.isRefreshing) {
                    val progress = offset / indicatorRefreshTrigger.coerceAtLeast(1f)

                    LinearOutSlowInEasing
                        .transform(progress)
                        .coerceIn(0f, 1f) // scaleを0~1fの範囲に制限する
                } else 1f

                scaleX = scaleFraction
                scaleY = scaleFraction
            },
        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
        color = MaterialTheme.colors.surface,
        elevation = adjustedElevation
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.isRefreshing) {
                val infiniteTransition = rememberInfiniteTransition()
                val slope = infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                Image(
                    painter = painterResource(R.drawable.weather_sunny),
                    contentDescription = "Refreshing",
                    modifier = Modifier
                        .size(indicatorImageSize)
                        .graphicsLayer {
                            rotationZ = slope.value
                        }
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.weather_sunny),
                    contentDescription = null,
                    modifier = Modifier
                        .size(indicatorImageSize)
                        .alpha(
                            (state.indicatorOffset / indicatorRefreshTrigger).coerceIn(0f, 1f)
                        )
                )
            }
        }
    }
}

private val indicatorSize = 56.dp
private val indicatorImageSize = 36.dp

/**
 * A utility function that calculates various aspects of 'slingshot' behavior.
 * Adapted from SwipeRefreshLayout#moveSpinner method.
 *
 * TODO: Investigate replacing this with a spring.
 *
 * @param offsetY The current y offset.
 * @param maxOffsetY The max y offset.
 * @param height The height of the item to slingshot.
 */
@Composable
internal fun rememberUpdatedSlingshotOffset(
    offsetY: Float,
    maxOffsetY: Float,
    height: Int
): MutableState<Int> {
    val offsetPercent = min(1f, offsetY / maxOffsetY)
    val extraOffset = abs(offsetY) - maxOffsetY

    // Can accommodate custom start and slingshot distance here
    val slingshotDistance = maxOffsetY
    val tensionSlingshotPercent = max(
        0f, min(extraOffset, slingshotDistance * 2) / slingshotDistance
    )
    val tensionPercent = (
            (tensionSlingshotPercent / 4) -
                    (tensionSlingshotPercent / 4).pow(2)
            ) * 2
    val extraMove = slingshotDistance * tensionPercent * 2
    val targetY = height + ((slingshotDistance * offsetPercent) + extraMove).toInt()
    val offset = targetY - height

    return remember {
        mutableStateOf(offset)
    }
}
