package pl.klolo.game.logic.bonus

import pl.klolo.game.physics.GameLighting
import pl.klolo.game.engine.ProfileHolder
import pl.klolo.game.engine.SoundEffect
import pl.klolo.game.event.*
import pl.klolo.game.physics.GamePhysics

class AdditionalLifeBonusLogic(
        profileHolder: ProfileHolder,
        eventProcessor: EventProcessor,
        gameLighting: GameLighting,
        gamePhysics: GamePhysics) : BaseBonusLogic(profileHolder, eventProcessor, gameLighting, gamePhysics) {

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return AddPlayerLife(20)
    }
}

class AdditionalPointsBonusLogic(
        profileHolder: ProfileHolder,
        val eventProcessor: EventProcessor,
        gameLighting: GameLighting,
        gamePhysics: GamePhysics) : BaseBonusLogic(profileHolder, eventProcessor, gameLighting, gamePhysics) {

    companion object {
        const val additionalPoints = 100
    }

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        eventProcessor.sendEvent(PlaySound(SoundEffect.YIPEE))
        return AddPoints(additionalPoints)
    }
}

class SuperBulletBonusLogic(
        profileHolder: ProfileHolder,
        eventProcessor: EventProcessor,
        gameLighting: GameLighting, gamePhysics: GamePhysics) : BaseBonusLogic(profileHolder, eventProcessor, gameLighting, gamePhysics) {

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return EnableSuperBullet
    }
}

class ShieldBonusLogic(
        profileHolder: ProfileHolder,
        eventProcessor: EventProcessor,
        gameLighting: GameLighting,
        gamePhysics: GamePhysics) : BaseBonusLogic(profileHolder, eventProcessor, gameLighting, gamePhysics) {

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return EnableShield
    }
}

class DoublePointsBonusLogic(
        profileHolder: ProfileHolder,
        eventProcessor: EventProcessor,
        gameLighting: GameLighting,
        gamePhysics: GamePhysics) : BaseBonusLogic(profileHolder, eventProcessor, gameLighting, gamePhysics) {

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return EnableDoublePoints
    }
}