package pl.klolo.spaceshooter.game.logic.player.move

import com.badlogic.gdx.Gdx
import pl.klolo.spaceshooter.game.engine.GameEngine
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.event.EventProcessor
import pl.klolo.spaceshooter.game.event.PlayerChangePosition
import kotlin.math.abs

class AndroidPlayerMoveLogic(
    private val eventProcessor: EventProcessor
) : PlayerMoveLogic, BasePlayerMove(eventProcessor) {

    private val accelerationFactor = GameEngine.applicationConfiguration.getConfig("android")
        .getConfig("player")
        .getDouble("accelerationFactor")
        .toFloat()

    override val initialize: SpriteEntityWithLogic.() -> Unit = {
        x = Gdx.graphics.width.toFloat() / 2 - width / 2
        y = 300.0f
    }

    override val createSubscription: SpriteEntityWithLogic.() -> EventProcessor.Subscription =
        { eventProcessor.subscribe(id) }

    override val onUpdate: SpriteEntityWithLogic.(Float) -> Unit = {
        move(Gdx.input.accelerometerX)
        super.checkBoundPosition(this)
        eventProcessor.sendEvent(PlayerChangePosition(x + width / 2, y + height / 2))
    }

    private fun SpriteEntityWithLogic.move(accelerometerX: Float) {
        val playerMoveDuration = getPlayerMoveDuration(accelerometerX, playerSpeed)

        if (playerMoveDuration == 0f) {
            direction = Direction.NONE
            removeAction(currentMove)
            return
        }

        if (accelerometerX < 0 && x < Gdx.graphics.width.toFloat()) {
            direction = Direction.RIGHT
            onMove(x + Gdx.graphics.width.toFloat(), playerMoveDuration)
        }

        if (accelerometerX > 0 && x > 0) {
            direction = Direction.LEFT
            onMove(x - Gdx.graphics.width.toFloat(), playerMoveDuration)
        }
    }
    fun getPlayerMoveDuration(accelerometerX: Float, speed: Float): Float {
        val playerAcceleration = abs(accelerometerX) * accelerationFactor
        val detectionLevel = 1f
        val maxDetectionLevel = 6f
        val detectionRange = maxDetectionLevel - detectionLevel
        val absX = abs(accelerometerX);

        if (absX < detectionLevel) {
            return 0f
        }

        if (absX >= maxDetectionLevel) {
            return speed
        }

        return (2 * speed - (absX / detectionRange) * speed) - playerAcceleration
    }
}