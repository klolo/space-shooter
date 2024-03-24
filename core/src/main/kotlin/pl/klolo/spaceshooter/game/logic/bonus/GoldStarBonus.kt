package pl.klolo.spaceshooter.game.logic.bonus

import com.badlogic.gdx.graphics.g2d.Sprite
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.engine.SoundEffect
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics
import pl.klolo.spaceshooter.game.logic.AddPoints
import pl.klolo.spaceshooter.game.logic.Event
import pl.klolo.spaceshooter.game.logic.PlaySound

class GoldStarBonus(
    val eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite,
) : AbstractBonus(eventBus, gameLighting, gamePhysics, entityConfiguration, sprite),
    BonusWithAdditionalPoints {

    override var additionalPoints: Int = 50

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        eventBus.sendEvent(PlaySound(SoundEffect.YIPEE))
        return AddPoints(additionalPoints)
    }
}