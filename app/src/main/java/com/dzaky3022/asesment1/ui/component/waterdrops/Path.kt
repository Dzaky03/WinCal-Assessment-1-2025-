package com.dzaky3022.asesment1.ui.component.waterdrops

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.IntSize
import com.dzaky3022.asesment1.utils.PointF
import com.dzaky3022.asesment1.utils.copy
import com.dzaky3022.asesment1.utils.lerpF
import com.dzaky3022.asesment1.utils.parabolaInterpolation
import com.dzaky3022.asesment1.utils.toBoolean
import com.dzaky3022.asesment1.ui.component.waterdrops.plottedpoints.createInitialMultipliersAsState
import com.dzaky3022.asesment1.ui.component.waterdrops.plottedpoints.createParabolaAsState
import com.dzaky3022.asesment1.ui.screen.ElementParams
import com.dzaky3022.asesment1.ui.screen.Paths


@Composable
fun createPathsAsState(
    levelState: LevelState,
    containerSize: IntSize,
    waterLevelProvider: () -> Float,
    dropWaterDuration: Int,
    animations: MutableList<State<Float>>,
    waveParams: com.dzaky3022.asesment1.ui.component.waterdrops.wave.WaveParams,
    elementParams: ElementParams,
): Paths {
    val parabola = createParabolaAsState(
        position = elementParams.position,
        elementSize = elementParams.size,
        waterLevel = waterLevelProvider(),
        buffer = waveParams.bufferY,
        dropWaterDuration = dropWaterDuration,
        levelState = levelState
    )

    val plottedPoints = createPlottedPointsAsState(
        waterLevel = waterLevelProvider(),
        containerSize = containerSize,
        levelState = levelState,
        position = elementParams.position,
        buffer = waveParams.bufferY,
        elementSize = elementParams.size,
        parabola = parabola.value,
        pointsQuantity = waveParams.pointsQuantity
    )

    val initialMultipliers =
        createInitialMultipliersAsState(pointsQuantity = waveParams.pointsQuantity)
    val waveMultiplier = animateFloatAsState(
        targetValue = if (levelState == LevelState.WaveIsComing) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = dropWaterDuration
            (0.7f).at((0.2f * dropWaterDuration).toInt())
            (0.8f).at((0.4f * dropWaterDuration).toInt())
        },
        label = "",
    )

    val paths by remember {
        mutableStateOf(Paths())
    }

    createPaths(
        animations,
        initialMultipliers,
        waveParams.maxWaveHeight,
        levelState,
        waveParams.bufferX,
        parabolaInterpolation(waveMultiplier.value),
        containerSize,
        plottedPoints,
        paths,
        elementParams
    )
    return paths
}

fun createPaths(
    animations: MutableList<State<Float>>,
    initialMultipliers: MutableList<Float>,
    maxHeight: Float,
    levelState: LevelState,
    bufferX: Float,
    waveMultiplier: Float = 1f,
    containerSize: IntSize,
    points: List<PointF>,
    paths: Paths,
    elementParams: ElementParams,
): Paths {

    for (i in 0..1) {
        var wavePoints = points.copy()
        val divider = i % 2
        wavePoints = addWaves(
            points = wavePoints,
            animations = animations,
            initialMultipliers = initialMultipliers,
            maxHeight = maxHeight,
            pointsInversion = divider.toBoolean(),
            levelState = levelState,
            position = elementParams.position,
            elementSize = elementParams.size,
            waveMultiplier = if (divider == 0) waveMultiplier / 2 else waveMultiplier,
            bufferX = bufferX,
        )
        paths.pathList[i].reset()
        paths.pathList[i] = createPath(containerSize, wavePoints, paths.pathList[i])
    }
    return paths
}

fun createPath(
    containerSize: IntSize,
    wavePoints: List<PointF>,
    path: Path
): Path {
    path.moveTo(0f, containerSize.height.toFloat())
    wavePoints.forEach {
        path.lineTo(it.x, it.y)
    }
    path.lineTo(containerSize.width.toFloat(), containerSize.height.toFloat())
    return path
}

fun addWaves(
    points: List<PointF>,
    animations: MutableList<State<Float>>,
    initialMultipliers: MutableList<Float>,
    maxHeight: Float,
    pointsInversion: Boolean,
    levelState: LevelState,
    position: Offset,
    elementSize: IntSize,
    bufferX: Float,
    waveMultiplier: Float,
): List<PointF> {
    val elementRangeX = (position.x - bufferX)..(position.x + elementSize.width + bufferX)
    points.forEachIndexed { index, pointF ->
        val newIndex = if (pointsInversion) {
            index % animations.size
        } else {
            (animations.size - index % animations.size) - 1
        }
        val initialMultipliersNewIndex = if (pointsInversion) {
            index
        } else {
            initialMultipliers.size - index - 1
        }
        var waveHeight = calculateWaveHeight(
            animations[newIndex].value,
            initialMultipliers[initialMultipliersNewIndex],
            maxHeight
        )

        if (levelState is LevelState.WaveIsComing && pointF.x in elementRangeX) {
            waveHeight *= waveMultiplier
        }

        pointF.y -= waveHeight
    }
    return points
}

private fun calculateWaveHeight(
    currentSize: Float,
    initialMultipliers: Float,
    maxHeight: Float
): Float {
    var waveHeightPercent = initialMultipliers + currentSize
    if (waveHeightPercent > 1.0f) {
        val diff = waveHeightPercent - 1.0f
        waveHeightPercent = 1.0f - diff
    }

    return lerpF(maxHeight, 0f, waveHeightPercent)
}