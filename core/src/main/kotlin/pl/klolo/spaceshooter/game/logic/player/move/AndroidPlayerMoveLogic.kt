package pl.klolo.game.logic.player.move

import com.badlogic.gdx.Gdx
import pl.klolo.game.engine.GameEngine
import pl.klolo.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.game.event.EventProcessor
import pl.klolo.game.event.PlayerChangePosition
import kotlin.math.abs

class AndroidPlayerMoveLogic(private val eventProcessor: EventProcessor) : PlayerMoveLogic,
    BasePlayerMove(eventProcessor) {

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
        val playerSpeed = getPlayerSpeed(accelerometerX, playerSpeed)

        if (playerSpeed == 0f) {
            direction = Direction.NONE
            removeAction(currentMove)
            return
        }

        val playerAcceleration = abs(accelerometerX) * accelerationFactor
        if (accelerometerX < 0 && x < Gdx.graphics.width.toFloat()) {
            direction = Direction.RIGHT
            onMove(x + Gdx.graphics.width.toFloat(), playerSpeed - playerAcceleration)
        }

        if (accelerometerX > 0 && x > 0) {
            direction = Direction.LEFT
            onMove(x - Gdx.graphics.width.toFloat(), playerSpeed - playerAcceleration)
        }
    }

    companion object {
        fun getPlayerSpeed(accelerometerX: Float, speed: Float): Float {
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

            return 2 * speed - (absX / detectionRange) * speed
        }
    }

}