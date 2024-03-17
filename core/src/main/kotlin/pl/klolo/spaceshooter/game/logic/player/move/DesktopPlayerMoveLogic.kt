package pl.klolo.spaceshooter.game.logic.player.move

import com.badlogic.gdx.Gdx
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.PlayerChangePosition
import pl.klolo.spaceshooter.game.event.KeyArrowLeftPressed
import pl.klolo.spaceshooter.game.event.KeyArrowLeftReleased
import pl.klolo.spaceshooter.game.event.KeyArrowRightPressed
import pl.klolo.spaceshooter.game.event.KeyArrowRightReleased

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
                .onEvent<KeyArrowLeftPressed> {
                    if (x - width > 0) {
                        direction = Direction.LEFT
                        onMove(x - Gdx.graphics.width.toFloat(), playerSpeed)
                    }
                }
                .onEvent<KeyArrowRightPressed> {
                    if (x + width < Gdx.graphics.width.toFloat()) {
                        direction = Direction.RIGHT
                        onMove(x + Gdx.graphics.width.toFloat(), playerSpeed)
                    }
                }
                .onEvent<KeyArrowRightReleased> {
                    if(direction == Direction.RIGHT) {
                        direction = Direction.NONE
                        removeAction(currentMove)
                    }
                }
                .onEvent<KeyArrowLeftReleased> {
                    if(direction == Direction.LEFT) {
                        direction = Direction.NONE
                        removeAction(currentMove)
                    }
                }
    }
}