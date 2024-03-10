package pl.klolo.game.logic

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.logic.player.move.AndroidPlayerMoveLogic

internal class AndroidPlayerMoveLogicTest : ShouldSpec({

    val sut = AndroidPlayerMoveLogic(EventBus())

    "stand in place when acceleroment is smaller than 1" {
        val speed = sut.getPlayerMoveDuration(0.1f, 10f)
        speed shouldBe 0f
    }

    "should go with max speed on left" {
        val speed = sut.getPlayerMoveDuration(10f, 10f)
        speed shouldBe 10f
    }

    "should go with max speed on right" {
        val speed = sut.getPlayerMoveDuration(-10f, 10f)
        speed shouldBe 10f
    }

    "should go on right" {
        val speed = sut.getPlayerMoveDuration(-2f, 10f)
        assert(speed > 13 && speed < 14)
    }

    "should go on left" {
        val speed = sut.getPlayerMoveDuration(-2.5f, 10f)
        assert(speed > 11 && speed < 12)
    }
})