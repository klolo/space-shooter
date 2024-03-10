package pl.klolo.spaceshooter.game.logic.bonus

import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.engine.ProfileHolder
import pl.klolo.spaceshooter.game.engine.SoundEffect
import pl.klolo.spaceshooter.game.physics.GamePhysics
import pl.klolo.spaceshooter.game.event.AddPlayerLife
import pl.klolo.spaceshooter.game.event.AddPoints
import pl.klolo.spaceshooter.game.event.EnableDoublePoints
import pl.klolo.spaceshooter.game.event.EnableShield
import pl.klolo.spaceshooter.game.event.EnableSuperBullet
import pl.klolo.spaceshooter.game.event.Event
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.PlaySound

class AdditionalLifeBonusLogic(
    profileHolder: ProfileHolder,
    eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics
) : BaseBonusLogic(profileHolder, eventBus, gameLighting, gamePhysics) {

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return AddPlayerLife(20)
    }
}

class AdditionalPointsBonusLogic(
    profileHolder: ProfileHolder,
    val eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics
) : BaseBonusLogic(profileHolder, eventBus, gameLighting, gamePhysics) {

    companion object {
        const val additionalPoints = 100
    }

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        eventBus.sendEvent(PlaySound(SoundEffect.YIPEE))
        return AddPoints(additionalPoints)
    }
}

class SuperBulletBonusLogic(
    profileHolder: ProfileHolder,
    eventBus: EventBus,
    gameLighting: GameLighting, gamePhysics: GamePhysics
) : BaseBonusLogic(profileHolder, eventBus, gameLighting, gamePhysics) {

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return EnableSuperBullet
    }
}

class ShieldBonusLogic(
    profileHolder: ProfileHolder,
    eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics
) : BaseBonusLogic(profileHolder, eventBus, gameLighting, gamePhysics) {

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return EnableShield
    }
}

class DoublePointsBonusLogic(
    profileHolder: ProfileHolder,
    eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics
) : BaseBonusLogic(profileHolder, eventBus, gameLighting, gamePhysics) {

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return EnableDoublePoints
    }
}