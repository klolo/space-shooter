package pl.klolo.spaceshooter.game.logic.player.move

import com.badlogic.gdx.Gdx
import pl.klolo.spaceshooter.game.engine.GameEngine
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.player.Player
import kotlin.math.abs

class AndroidPlayerMoveStrategy(
    private val player: Player,
    eventBus: EventBus
) : AbstractPlayerMoveStrategy(player, eventBus) {

    private var accelerationFactor = 0f

    override fun initialize() {
        player.x = Gdx.graphics.width.toFloat() / 2 - player.width / 2
        player.y = 300.0f

        accelerationFactor = GameEngine.applicationConfiguration.getConfig("android")
            .getConfig("player")
            .getDouble("accelerationFactor")
            .toFloat()
    }

    override fun subscribeEvents(eventBus: EventBus) =
        eventBus.subscribe(player.id)

    override fun update(delta: Float) {
        move(Gdx.input.accelerometerX)
        super.update(delta)
    }

    private fun move(accelerometerX: Float) {
        val playerMoveDuration = getPlayerMoveDuration(accelerometerX, playerSpeed)

        if (playerMoveDuration == 0f) {
            direction = Direction.NONE
            player.removeAction(currentMove)
            return
        }

        if (accelerometerX < 0 && player.x < Gdx.graphics.width.toFloat()) {
            direction = Direction.RIGHT
            moveX(player.x + Gdx.graphics.width.toFloat(), playerMoveDuration)
        }

        if (accelerometerX > 0 && player.x > 0) {
            direction = Direction.LEFT
            moveX(player.x - Gdx.graphics.width.toFloat(), playerMoveDuration)
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

    override fun playerBottomMargin() = player.height * 0.8f

}