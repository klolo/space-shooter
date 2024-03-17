package pl.klolo.game.common

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import pl.klolo.spaceshooter.game.common.moveToPoint

internal class MathExtensionsKtTest : StringSpec({
    "should move position on x axis" {
        val startPoint = 0f to 0f
        val targetPoint = 10f to 0f
        val distance = 10f

        val expectedPosition = 10f to 0f

        moveToPoint(startPoint, targetPoint, distance) shouldBe expectedPosition
    }

    "should move position on y axis" {
        val startPoint = 0f to 0f
        val targetPoint = 0f to 10f
        val distance = 10f

        val expectedPosition = 0f to 10f

        moveToPoint(startPoint, targetPoint, distance) shouldBe expectedPosition
    }

    "should move position on both axis" {
        val startPoint = 0f to 0f
        val targetPoint = 10f to 10f
        val distance = 16f

        val expectedPosition = 11.313709f to 11.313709f

        moveToPoint(startPoint, targetPoint, distance) shouldBe expectedPosition
    }
})
