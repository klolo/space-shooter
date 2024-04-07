package pl.klolo.spaceshooter.game.logic.bonus

import com.badlogic.gdx.graphics.g2d.Sprite
import pl.klolo.spaceshooter.game.engine.physics.GameLighting
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics
import pl.klolo.spaceshooter.game.logic.EnableSuperBullet
import pl.klolo.spaceshooter.game.logic.Event

class SuperBulletBonus(
    eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite,
) : AbstractBonus(eventBus, gameLighting, gamePhysics, entityConfiguration, sprite) {

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return EnableSuperBullet
    }
}