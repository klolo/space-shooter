package pl.klolo.spaceshooter.game.logic.player

import box2dLight.PointLight
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.Action
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.common.Colors
import pl.klolo.spaceshooter.game.common.Colors.blueLight
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.logic.Highscore
import pl.klolo.spaceshooter.game.engine.Profile
import pl.klolo.spaceshooter.game.engine.ProfileHolder
import pl.klolo.spaceshooter.game.engine.SoundEffect
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.entity.isEnemyLaser
import pl.klolo.spaceshooter.game.engine.entity.isExtraPointsBonus
import pl.klolo.spaceshooter.game.engine.entity.kind.ParticleEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.logic.AddPlayerLife
import pl.klolo.spaceshooter.game.logic.AddPoints
import pl.klolo.spaceshooter.game.logic.ChangePlayerLfeLevel
import pl.klolo.spaceshooter.game.logic.Collision
import pl.klolo.spaceshooter.game.logic.DisableDoublePoints
import pl.klolo.spaceshooter.game.logic.DisableShield
import pl.klolo.spaceshooter.game.logic.DisableSuperBullet
import pl.klolo.spaceshooter.game.logic.EnableDoublePoints
import pl.klolo.spaceshooter.game.logic.EnableShield
import pl.klolo.spaceshooter.game.logic.EnableSuperBullet
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.GameOver
import pl.klolo.spaceshooter.game.logic.KeySpaceReleased
import pl.klolo.spaceshooter.game.logic.PlaySound
import pl.klolo.spaceshooter.game.logic.RegisterEntity
import pl.klolo.spaceshooter.game.logic.StopMusic
import pl.klolo.spaceshooter.game.logic.Bullet
import pl.klolo.spaceshooter.game.logic.bonus.BonusWithAdditionalPoints
import pl.klolo.spaceshooter.game.logic.enemy.ExplosionEffect
import pl.klolo.spaceshooter.game.logic.helper.PopupMessageConfiguration
import pl.klolo.spaceshooter.game.logic.helper.PopupMessages
import pl.klolo.spaceshooter.game.logic.player.move.AbstractPlayerMoveStrategy
import pl.klolo.spaceshooter.game.logic.player.move.createMoveStrategy
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics

const val bonusLifetime = 20f

class Player(
    private val profileHolder: ProfileHolder,
    private val highscore: Highscore,
    private val gamePhysics: GamePhysics,
    private val entityRegistry: EntityRegistry,
    private val eventBus: EventBus,
    private val gameLighting: GameLighting,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : SpriteEntity(entityConfiguration, sprite) {

    private var explosionLights = ExplosionEffect(gameLighting, 100f)
    private val popupMessages = PopupMessages(entityRegistry, eventBus)
    private lateinit var moveStrategy: AbstractPlayerMoveStrategy
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

    override fun onDispose() {
        physicsShape.dispose()
        explosionLights.onDispose()
        gamePhysics.destroy(body)
    }

    override fun onInitialize() {
        Gdx.app.debug(this.javaClass.name, "createSubscription")
        y = getPlayerBottomMargin(profileHolder.activeProfile, height)

        playerLight = gameLighting.createPointLight(100, blueLight, 50f, x, y)
        laserConfiguration = entityRegistry.getConfigurationById("laserBlue01")

        createPhysics()
        createEngineFire()

        moveStrategy = createMoveStrategy(profileHolder.activeProfile, this)
        moveStrategy.initialize()
        moveStrategy.subscribeEvents(eventBus)

        eventBus.subscribe(id)
            .onEvent<Collision> { onCollision(it) }
            .onEvent<KeySpaceReleased> { shootOnPosition() }
            .onEvent<AddPoints> { addPoints(it) }
            .onEvent<AddPlayerLife> { onAddPlayerLife(it) }
            .onEvent<EnableSuperBullet> { onEnableSuperBullet() }
            .onEvent<EnableShield> { onEnableShield() }
            .onEvent<EnableDoublePoints> { onEnableDoublePoints() }
    }

    private fun onEnableDoublePoints() {
        eventBus.sendEvent(PlaySound(SoundEffect.FOUND_BONUS))
        doublePoints = true

        popupMessages.show(this, PopupMessageConfiguration("x2"))
        executeAfterDelay(bonusLifetime) {
            doublePoints = false
            popupMessages.show(this, PopupMessageConfiguration("x1"))
            eventBus.sendEvent(DisableDoublePoints)
        }
    }

    private fun onEnableShield() {
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

    private fun onEnableSuperBullet() {
        eventBus.sendEvent(PlaySound(SoundEffect.FOUND_BONUS))
        enableSuperBullet()
        executeAfterDelay(bonusLifetime) { disableSuperBullet() }
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

    private fun onAddPlayerLife(it: AddPlayerLife) {
        Gdx.app.debug(this.javaClass.name, "increase life level $it")
        lifeLevel += it.lifeAmount
        if (lifeLevel > 100) {
            lifeLevel = 100
        }
        else {
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

    private fun onCollision(it: Collision) {
        val collidedEntity = it.entity!!
        if (isEnemyLaser(collidedEntity) && !hasShield && !isImmortal) {
            eventBus.sendEvent(PlaySound(SoundEffect.PLAYER_COLLISION))

            lifeLevel -= 10
            eventBus.sendEvent(PlaySound(SoundEffect.PLAYER_HIT))
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
            val bonusEntityWithLogic = ((collidedEntity as SpriteEntity))
            val starBonus = bonusEntityWithLogic as BonusWithAdditionalPoints
            popupMessages.show(this, PopupMessageConfiguration("+${starBonus.additionalPoints}"))
        }
    }

    private fun onGameOver() {
        highscore.setLastScore(points)

        eventBus.sendEvent(StopMusic)
        eventBus.sendEvent(PlaySound(SoundEffect.DESTROY_PLAYER))

        playerLight.distance *= 200
        display = false

        executeAfterDelay(0.15f) {
            eventBus.sendEvent(GameOver)
        }
    }

    private fun shootOnPosition() {
        val bulletEntity = createEntity<Bullet>(laserConfiguration)
        bulletEntity.isEnemyBullet = false
        bulletEntity.bulletPower = bulletPower
        bulletEntity.x = x + width / 2
        bulletEntity.y = y + height / 2
        bulletEntity.onInitialize()

        if (enabledSuperBulletCounter > 0) {
            bulletEntity.apply {
                bulletLight.color = Colors.gold
                bulletLight.distance = 2f * bulletLight.distance
            }
        }

        eventBus.sendEvent(RegisterEntity(bulletEntity))
        eventBus.sendEvent(PlaySound(SoundEffect.PLAYER_SHOOT))
    }

    override fun onUpdate(delta: Float) {
        popupMessages.updatePosition(x + width / 2, y + height)
        playerLight.setPosition(x + width / 2, y + height / 2)
        body.setTransform(x + width / 2, y + height / 2, 0.0f)
        moveStrategy.update(delta)
        updateEngineFirePosition()
        super.onUpdate(delta)
    }

    private fun updateEngineFirePosition() {
        val currentX = x
        val currentY = y
        val playerWidth = width
        engineFire.apply {
            this.effect.setPosition(currentX + playerWidth / 2, currentY)
        }
    }

    private fun createPhysics() {
        physicsShape = PolygonShape()
        physicsShape.setAsBox(width / 2, height / 2);
        body = gamePhysics.createDynamicBody()
        body.createFixture(gamePhysics.getStandardFixtureDef(physicsShape)).userData = this
    }

    private fun getPlayerBottomMargin(profile: Profile, playerHeight: Float): Float {
        return when (profile) {
            Profile.ANDROID -> playerHeight * 0.8f
            else -> playerHeight
        }
    }
}