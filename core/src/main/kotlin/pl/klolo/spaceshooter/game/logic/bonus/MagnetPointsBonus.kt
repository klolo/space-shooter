package pl.klolo.spaceshooter.game.logic.bonus

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.common.addSequence
import pl.klolo.spaceshooter.game.common.execute
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics
import pl.klolo.spaceshooter.game.logic.DisableMagnetPoints
import pl.klolo.spaceshooter.game.logic.EnableMagnetPoints
import pl.klolo.spaceshooter.game.logic.Event
import pl.klolo.spaceshooter.game.logic.player.bonusLifetime

class MagnetPointsBonus(
    var eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : AbstractBonus(eventBus, gameLighting, gamePhysics, entityConfiguration, sprite) {

    override fun onCollisionWithPlayer() {
        ignoreNextCollision = true
        clearActions()

        addSequence(
            Actions.scaleTo(0.01f, 0.01f, 0.2f, Interpolation.linear),
            execute {
                eventBus.sendEvent(getEventToSendOnCollisionWithPlayer())
                shouldBeRemove = true
            }
        )

        executeAfterDelay(bonusLifetime) {
            eventBus.sendEvent(DisableMagnetPoints)
        }
    }

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return EnableMagnetPoints
    }

}


