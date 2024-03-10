package pl.klolo.spaceshooter.game.logic.player

import box2dLight.PointLight
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.Action
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.common.Colors
import pl.klolo.spaceshooter.game.common.Colors.blueLight
import pl.klolo.spaceshooter.game.entity.kind.ParticleEntity
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.logic.BulletLogic
import pl.klolo.spaceshooter.game.logic.bonus.AdditionalPointsBonusLogic.Companion.additionalPoints
import pl.klolo.spaceshooter.game.logic.enemy.ExplosionEffect
import pl.klolo.spaceshooter.game.logic.helper.PopupMessageConfiguration
import pl.klolo.spaceshooter.game.logic.helper.PopupMessages
import pl.klolo.spaceshooter.game.logic.player.move.getMoveLogicImplementation
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.physics.GamePhysics
import pl.klolo.spaceshooter.game.engine.Highscore
import pl.klolo.spaceshooter.game.engine.ProfileHolder
import pl.klolo.spaceshooter.game.engine.SoundEffect
import pl.klolo.spaceshooter.game.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.entity.EntityLogic
import pl.klolo.spaceshooter.game.entity.EntityRegistry
import pl.klolo.spaceshooter.game.entity.createEntity
import pl.klolo.spaceshooter.game.entity.isEnemyLaser
import pl.klolo.spaceshooter.game.entity.isExtraPointsBonus
import pl.klolo.spaceshooter.game.event.AddPlayerLife
import pl.klolo.spaceshooter.game.event.AddPoints
import pl.klolo.spaceshooter.game.event.ChangePlayerLfeLevel
import pl.klolo.spaceshooter.game.event.Collision
import pl.klolo.spaceshooter.game.event.DisableDoublePoints
import pl.klolo.spaceshooter.game.event.DisableShield
import pl.klolo.spaceshooter.game.event.DisableSuperBullet
import pl.klolo.spaceshooter.game.event.EnableDoublePoints
import pl.klolo.spaceshooter.game.event.EnableShield
import pl.klolo.spaceshooter.game.event.EnableSuperBullet
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.GameOver
import pl.klolo.spaceshooter.game.event.PlaySound
import pl.klolo.spaceshooter.game.event.PressedSpace
import pl.klolo.spaceshooter.game.event.RegisterEntity
import pl.klolo.spaceshooter.game.event.StopMusic

const val bonusLifetime = 20f

class PlayerLogic(
    private val profileHolder: ProfileHolder,
    private val highscore: Highscore,
    private val gamePhysics: GamePhysics,
    private val entityRegistry: EntityRegistry,
    private val eventBus: EventBus,
    private val gameLighting: GameLighting
) : EntityLogic<SpriteEntityWithLogic> {

    private var explosionLights = ExplosionEffect(gameLighting, 100f)
    private val popupMessages = PopupMessages(entityRegistry, eventBus)
    private var moveLogic = getMoveLogicImplementation(profileHolder.activeProfile, eventBus)
    private lateinit var engineFire: ParticleEntity

    private var hasShield = false

    private var lifeLevel = 100
    private var points = 0
    private val defaultBulletPower = 10
    private var enabledSuperBulletCounter = 0
    private var bulletPower = defaultBulletPower
    private var doublePoints = false
    private var isImmortal = false

    private lateinit var physicsShape: PolygonShape
    private lateinit var body: Body
    private lateinit var playerLight: PointLight
    private lateinit var laserConfiguration: EntityConfiguration
    private var disableShieldAction: Action? = null

    override val onDispose: SpriteEntityWithLogic.() -> Unit = {
        physicsShape.dispose()
        explosionLights.onDispose()
        gamePhysics.destroy(body)
    }

    override val initialize: SpriteEntityWithLogic.() -> Unit = {
        Gdx.app.debug(this.javaClass.name, "createSubscription")
        y = getPlayerBottomMargin(profileHolder.activeProfile, height)

        playerLight = gameLighting.createPointLight(100, blueLight, 50f, x, y)
        laserConfiguration = entityRegistry.getConfigurationById("laserBlue01")

        moveLogic.initialize(this)

        moveLogic
            .createSubscription(this)
            .onEvent<Collision> {
                onCollision(it)
            }
            .onEvent<PressedSpace> {
                shootOnPosition()
            }
            .onEvent<AddPoints> {
                addPoints(it)
            }
            .onEvent<AddPlayerLife> {
                onAddPlayerLife(it)
            }
            .onEvent<EnableSuperBullet> {
                eventBus.sendEvent(PlaySound(SoundEffect.FOUND_BONUS))
                enableSuperBullet()
                executeAfterDelay(bonusLifetime) { disableSuperBullet() }
            }
            .onEvent<EnableShield> {
                eventBus.sendEvent(PlaySound(SoundEffect.FOUND_BONUS))
                hasShield = true

                if (disableShieldAction != null) {
                    removeAction(disableShieldAction)
                }

                disableShieldAction = executeAfterDelay(bonusLifetime) {
                    hasShield = false
                    eventBus.sendEvent(DisableShield)
                }
            }
            .onEvent<EnableDoublePoints> {
                eventBus.sendEvent(PlaySound(SoundEffect.FOUND_BONUS))
                doublePoints = true

                popupMessages.show(this, PopupMessageConfiguration("x2"))
                executeAfterDelay(bonusLifetime) {
                    doublePoints = false
                    popupMessages.show(this, PopupMessageConfiguration("x1"))
                    eventBus.sendEvent(DisableDoublePoints)
                }
            }

        createPhysics()
        createEngineFire()
    }

    private fun createEngineFire() {
        val engineFireConfiguration = entityRegistry.getConfigurationById("engineFire")
        engineFire = createEntity(engineFireConfiguration)
        eventBus.sendEvent(RegisterEntity(engineFire))
    }

    private fun addPoints(it: AddPoints) {
        points = when (doublePoints) {
            true -> points + (it.points * 2)
            false -> points + it.points
        }
    }

    private fun SpriteEntityWithLogic.onAddPlayerLife(it: AddPlayerLife) {
        Gdx.app.debug(this.javaClass.name, "increase life level $it")
        lifeLevel += it.lifeAmount
        if (lifeLevel > 100) {
            lifeLevel = 100
        } else {
            eventBus.sendEvent(PlaySound(SoundEffect.YIPEE))
        }

        eventBus.sendEvent(ChangePlayerLfeLevel(lifeLevel))
        popupMessages.show(this, PopupMessageConfiguration("+${it.lifeAmount}%"))
    }

    private fun enableSuperBullet() {
        Gdx.app.debug(
            this.javaClass.name,
            "Enable super bullet. enabled: $enabledSuperBulletCounter, current power: $bulletPower"
        )
        laserConfiguration = entityRegistry.getConfigurationById("laserBlue02")
        bulletPower *= 4
        enabledSuperBulletCounter++
        playerLight.distance = 150f
        playerLight.color = Colors.gold
    }

    private fun disableSuperBullet() {
        enabledSuperBulletCounter--
        if (enabledSuperBulletCounter == 0) {
            Gdx.app.debug(this.javaClass.name, "Disable super bullet.")
            laserConfiguration = entityRegistry.getConfigurationById("laserBlue01")
            bulletPower = defaultBulletPower
            playerLight.distance = 70f
            playerLight.color = blueLight
            eventBus.sendEvent(DisableSuperBullet)
        }
    }

    private fun SpriteEntityWithLogic.onCollision(it: Collision) {
        val collidedEntity = it.entity!!
        if (isEnemyLaser(collidedEntity) && !hasShield && !isImmortal) {
            eventBus.sendEvent(PlaySound(SoundEffect.PLAYER_COLLISION))

            lifeLevel -= 10
            popupMessages.show(this, PopupMessageConfiguration("-10%", Colors.orange))

            Gdx.input.vibrate(150)

            executeAfterDelay(0.2f) {
                isImmortal = false
            }

            explosionLights.addLight(this)
            eventBus.sendEvent(ChangePlayerLfeLevel(lifeLevel))
            isImmortal = true

            if (lifeLevel <= 0) {
                Gdx.input.vibrate(300)
                onGameOver()
            }
        }

        if (isExtraPointsBonus(collidedEntity)) {
            popupMessages.show(this, PopupMessageConfiguration("+$additionalPoints"))
        }
    }

    private fun SpriteEntityWithLogic.onGameOver() {
        highscore.setLastScore(points)

        eventBus.sendEvent(StopMusic)
        eventBus.sendEvent(PlaySound(SoundEffect.DESTROY_PLAYER))

        playerLight.distance *= 200
        display = false

        executeAfterDelay(0.15f) {
            eventBus.sendEvent(GameOver)
        }
    }

    private fun SpriteEntityWithLogic.shootOnPosition() {
        val bulletXPosition = x + width / 2
        val bulletYPosition = y + height / 2

        val bulletEntity: SpriteEntityWithLogic = createEntity(laserConfiguration) {
            x = bulletXPosition
            y = bulletYPosition
        }

        (bulletEntity.logic as BulletLogic).isEnemyBullet = false
        bulletEntity.logic.bulletPower = bulletPower

        if (enabledSuperBulletCounter > 0) {
            bulletEntity.logic.apply {
                bulletLight.color = Colors.gold
                bulletLight.distance = 2f * bulletLight.distance
            }
        }

        eventBus.sendEvent(RegisterEntity(bulletEntity))
        eventBus.sendEvent(PlaySound(SoundEffect.PLAYER_SHOOT))
    }

    override val onUpdate: SpriteEntityWithLogic.(Float) -> Unit = {
        popupMessages.updatePosition(x + width / 2, y + height)
        playerLight.setPosition(x + width / 2, y + height / 2)
        body.setTransform(x + width / 2, y + height / 2, 0.0f)
        moveLogic.onUpdate(this, it)
        updateEngineFirePosition()
    }

    private fun SpriteEntityWithLogic.updateEngineFirePosition() {
        val currentX = x
        val currentY = y
        val playerWidth = width
        engineFire.apply {
            this.effect.setPosition(currentX + playerWidth / 2, currentY)
        }
    }

    private fun SpriteEntityWithLogic.createPhysics() {
        physicsShape = PolygonShape()
        physicsShape.setAsBox(width / 2, height / 2);
        body = gamePhysics.createDynamicBody()

        body.createFixture(gamePhysics.getStandardFixtureDef(physicsShape)).userData = this
    }
}