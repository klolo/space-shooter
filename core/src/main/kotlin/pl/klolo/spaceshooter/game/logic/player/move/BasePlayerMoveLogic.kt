package pl.klolo.spaceshooter.game.logic.player.move

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo
import pl.klolo.spaceshooter.game.engine.Profile
import pl.klolo.spaceshooter.game.engine.GameEngine.Companion.applicationConfiguration
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.common.execute


enum class Direction { LEFT, RIGHT, NONE }

interface PlayerMoveLogic {
    val initialize: SpriteEntityWithLogic.() -> Unit
    val onUpdate: SpriteEntityWithLogic.(Float) -> Unit
    val createSubscription: SpriteEntityWithLogic.() -> EventBus.Subscription
}

fun getMoveLogicImplementation(profile: Profile, eventBus: EventBus): PlayerMoveLogic {
    return when (profile) {
        Profile.ANDROID -> AndroidPlayerMoveLogic(eventBus)
        Profile.DESKTOP -> DesktopPlayerMoveLogic(eventBus)
        else -> throw IllegalArgumentException("Profile not supported")
    }
}

abstract class BasePlayerMove(private val eventBus: EventBus) {

    protected val playerSpeed = applicationConfiguration.getConfig("engine")
            .getDouble("playerSpeed")
            .toFloat()    // seconds per screen width

    protected var currentMove: Action = execute {}
    protected var direction = Direction.NONE
    protected val margin = 35f

    protected val checkBoundPosition: SpriteEntityWithLogic.() -> Unit = {
        val centerX = x + width / 2
        val stopMovingLeft = centerX < margin && direction == Direction.LEFT
        val stopMovingRight = centerX > Gdx.graphics.width.toFloat() - margin && direction == Direction.RIGHT

        if (stopMovingLeft || stopMovingRight) {
            removeAction(currentMove)
            direction = Direction.NONE
        }

        if (x > Gdx.graphics.width.toFloat()) {
            x = Gdx.graphics.width.toFloat() - width
        }
    }

    protected fun SpriteEntityWithLogic.onMove(x: Float, moveDuration: Float) {
        removeAction(currentMove)
        currentMove = moveTo(x, y, moveDuration)
        addAction(currentMove)
    }

}