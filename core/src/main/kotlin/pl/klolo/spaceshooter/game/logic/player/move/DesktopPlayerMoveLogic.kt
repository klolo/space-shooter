package pl.klolo.spaceshooter.game.logic.player.move

import com.badlogic.gdx.Gdx
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.PlayerChangePosition
import pl.klolo.spaceshooter.game.event.PressedLeftDown
import pl.klolo.spaceshooter.game.event.PressedLeftUp
import pl.klolo.spaceshooter.game.event.PressedRightDown
import pl.klolo.spaceshooter.game.event.PressedRightUp

class DesktopPlayerMoveLogic(private val eventBus: EventBus) : PlayerMoveLogic, BasePlayerMove(eventBus) {

    override val initialize: SpriteEntityWithLogic.() -> Unit = {
        x = Gdx.graphics.width.toFloat() / 2 - width / 2
    }

    override val onUpdate: SpriteEntityWithLogic.(Float) -> Unit = {
        super.checkBoundPosition(this)
        eventBus.sendEvent(PlayerChangePosition(x + width / 2, y + height / 2))
    }
    override val createSubscription: SpriteEntityWithLogic.() -> EventBus.Subscription = {
        eventBus
                .subscribe(id)
                .onEvent<PressedLeftDown> {
                    if (x - width > 0) {
                        direction = Direction.LEFT
                        onMove(x - Gdx.graphics.width.toFloat(), playerSpeed)
                    }
                }
                .onEvent<PressedRightDown> {
                    if (x + width < Gdx.graphics.width.toFloat()) {
                        direction = Direction.RIGHT
                        onMove(x + Gdx.graphics.width.toFloat(), playerSpeed)
                    }
                }
                .onEvent<PressedRightUp> {
                    direction = Direction.NONE
                    removeAction(currentMove)
                }
                .onEvent<PressedLeftUp> {
                    direction = Direction.NONE
                    removeAction(currentMove)
                }
    }
}