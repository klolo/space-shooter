package pl.klolo.spaceshooter.game.logic.bonus

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.common.addSequence
import pl.klolo.spaceshooter.game.common.execute
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.common.getScreenHeight
import pl.klolo.spaceshooter.game.common.moveToPoint
import pl.klolo.spaceshooter.game.engine.ProfileHolder
import pl.klolo.spaceshooter.game.engine.SoundEffect
import pl.klolo.spaceshooter.game.engine.sounds
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.event.AddPlayerLife
import pl.klolo.spaceshooter.game.event.AddPoints
import pl.klolo.spaceshooter.game.event.DisableMagnetPoints
import pl.klolo.spaceshooter.game.event.EnableDoublePoints
import pl.klolo.spaceshooter.game.event.EnableMagnetPoints
import pl.klolo.spaceshooter.game.event.EnableShield
import pl.klolo.spaceshooter.game.event.EnableSuperBullet
import pl.klolo.spaceshooter.game.event.Event
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.PlaySound
import pl.klolo.spaceshooter.game.event.PlayerChangePosition
import pl.klolo.spaceshooter.game.logic.player.bonusLifetime
import pl.klolo.spaceshooter.game.physics.GamePhysics
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.sqrt

class AdditionalLifeBonusLogic(
    profileHolder: ProfileHolder,
    eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics
) : BaseBonusLogic(profileHolder, eventBus, gameLighting, gamePhysics) {

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        return AddPlayerLife(10)
    }
}

class SilverStarBonusLogic(
    profileHolder: ProfileHolder,
    val eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics,
) : BaseBonusLogic(profileHolder, eventBus, gameLighting, gamePhysics), BonusWithAdditionalPoints {

    override var additionalPoints = 10

    /**
     * Configuration for all stars
     */
    companion object {
        private val initialSpeed = 1f

        private val maxSpeed = 2.5f

        private var playerPosX: Float = 0f

        private var playerPosY: Float = 0f

        private var bonusSpeedBasedOnMagnet = initialSpeed

        private var subscription: EventBus.Subscription? = null

        private var magnetCounter = AtomicInteger(0)
    }

    init {
        synchronized(this) {
            if (subscription == null) {
                subscription = eventBus
                    .subscribe(this.hashCode())
                    .onEvent<PlayerChangePosition> {
                        playerPosX = it.x
                        playerPosY = it.y
                    }
                    .onEvent<EnableMagnetPoints> {
                        bonusSpeedBasedOnMagnet = maxSpeed
                        magnetCounter.incrementAndGet()
                        eventBus.sendEvent(PlaySound(SoundEffect.FOUND_BONUS))
                    }
                    .onEvent<DisableMagnetPoints> {
                        if (magnetCounter.decrementAndGet() <= 0) {
                            bonusSpeedBasedOnMagnet = initialSpeed
                        }
                    }
            }
        }
    }

    override fun getEventToSendOnCollisionWithPlayer(): Event {
        eventBus.sendEvent(PlaySound(SoundEffect.YIPEE))
        return AddPoints(additionalPoints)
    }

    override val onUpdate: SpriteEntityWithLogic.(Float) -> Unit = {
        // TODO: this should be in overrided initilize method
        if (bonusMoveAction != null) {
            removeAction(bonusMoveAction)
            bonusMoveAction = null
        }

        val magnetEnabled = true // magnetCounter.decrementAndGet() > 0
        val followPlayer = magnetEnabled && y >= playerPosY + 50

        val newPosition =
            if (followPlayer) moveToPoint(x to y, playerPosX to playerPosY, bonusSpeedBasedOnMagnet)
            else moveToPoint(x to y, x to y - 100, bonusSpeedBasedOnMagnet)

        x = newPosition.first
        y = newPosition.second

        light.setPosition(x + width / 2, y + height / 2)
        body.setTransform(x + width / 2, y + height / 2, 0.0f)

        if (y < -100) {
            shouldBeRemove = true
        }
    }

}

class GoldStarBonusLogic(
    profileHolder: ProfileHolder,
    val eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics,
) : BaseBonusLogic(profileHolder, eventBus, gameLighting, gamePhysics), BonusWithAdditionalPoints {

    override var additionalPoints: Int = 50
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

class MagnetPointsBonusLogic(
    profileHolder: ProfileHolder,
    eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics
) : BaseBonusLogic(profileHolder, eventBus, gameLighting, gamePhysics) {

    override var onCollisionWithPlayer: SpriteEntityWithLogic.() -> Unit = {
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


