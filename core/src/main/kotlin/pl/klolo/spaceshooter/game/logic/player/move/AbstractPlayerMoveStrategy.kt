package pl.klolo.spaceshooter.game.logic.player.move

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo
import pl.klolo.spaceshooter.game.common.execute
import pl.klolo.spaceshooter.game.engine.GameEngine.Companion.applicationConfiguration
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.PlayerChangePosition
import pl.klolo.spaceshooter.game.logic.player.Player

abstract class AbstractPlayerMoveStrategy(
    private val player: Player,
    private val eventBus: EventBus
) {

    protected var playerSpeed: Float = 0f

    protected var currentMove: Action = execute {}

    protected var direction = Direction.NONE

    protected val margin = 35f

    abstract fun subscribeEvents(eventBus: EventBus): EventBus.Subscription

    open fun initialize() {

    }

    open fun update(delta: Float) {
        val centerX = player.x + player.width / 2
        val stopMovingLeft = centerX < margin && direction == Direction.LEFT
        val stopMovingRight =
            centerX > Gdx.graphics.width.toFloat() - margin && direction == Direction.RIGHT

        if (stopMovingLeft || stopMovingRight) {
            player.removeAction(currentMove)
            direction = Direction.NONE
        }

        if (player.x > Gdx.graphics.width.toFloat()) {
            player.x = Gdx.graphics.width.toFloat() - player.width
        }

        eventBus.sendEvent(PlayerChangePosition(player.x, player.y))
    }

    protected fun move(x: Float, moveDuration: Float) {
        player.x += 1f;
        player.removeAction(currentMove)
        currentMove = moveTo(x, player.y, moveDuration)
        player.addAction(currentMove)
    }

}