package pl.klolo.spaceshooter.game.logic.bonus

import com.badlogic.gdx.graphics.g2d.Sprite
import pl.klolo.spaceshooter.game.engine.physics.GameLighting
import pl.klolo.spaceshooter.game.common.moveToPoint
import pl.klolo.spaceshooter.game.engine.SoundEffect
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics
import pl.klolo.spaceshooter.game.logic.AddPoints
import pl.klolo.spaceshooter.game.logic.DisableMagnetPoints
import pl.klolo.spaceshooter.game.logic.EnableMagnetPoints
import pl.klolo.spaceshooter.game.logic.Event
import pl.klolo.spaceshooter.game.logic.PlaySound
import pl.klolo.spaceshooter.game.logic.PlayerChangePosition
import java.util.concurrent.atomic.AtomicInteger


class SilverStarBonus(
    val eventBus: EventBus,
    gameLighting: GameLighting,
    gamePhysics: GamePhysics,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite,
) : AbstractBonus(eventBus, gameLighting, gamePhysics, entityConfiguration, sprite),
    BonusWithAdditionalPoints {

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
                    .subscribe(id)
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

    override fun onUpdate(delta: Float) {
        // TODO: this should be in overrided initilize method
        if (bonusMoveAction != null) {
            removeAction(bonusMoveAction)
            bonusMoveAction = null
        }

        val magnetEnabled = magnetCounter.decrementAndGet() > 0
        val followPlayer = magnetEnabled && y < playerPosY + 50

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

        super.onUpdate(delta)
    }

}