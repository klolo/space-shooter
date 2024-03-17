package pl.klolo.spaceshooter.game.common

import kotlin.math.sqrt

fun moveToPoint(
    startPoint: Pair<Float, Float>,
    targetPoint: Pair<Float, Float>,
    distance: Float
): Pair<Float, Float> {
    val dx = targetPoint.first - startPoint.first
    val dy = targetPoint.second - startPoint.second
    val length = sqrt(dx * dx + dy * dy)

    val newX = startPoint.first + (distance / length) * dx
    val newY = startPoint.second + (distance / length) * dy

    return newX to newY
}