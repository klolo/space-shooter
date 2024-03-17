package pl.klolo.spaceshooter.game.logic.enemy

import box2dLight.PointLight
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.entity.isPlayerLaser
import pl.klolo.spaceshooter.game.entity.kind.ParticleEntity
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.logic.BulletLogic
import pl.klolo.spaceshooter.game.logic.Direction
import pl.klolo.spaceshooter.game.logic.helper.PopupMessageConfiguration
import pl.klolo.spaceshooter.game.logic.helper.PopupMessages
import pl.klolo.spaceshooter.game.physics.GamePhysics
import pl.klolo.spaceshooter.game.common.Colors
import pl.klolo.spaceshooter.game.common.addForeverSequence
import pl.klolo.spaceshooter.game.common.addSequence
import pl.klolo.spaceshooter.game.common.execute
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.common.getScreenHeight
import pl.klolo.spaceshooter.game.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.entity.EntityLogic
import pl.klolo.spaceshooter.game.entity.EntityRegistry
import pl.klolo.spaceshooter.game.entity.createEntity
import pl.klolo.spaceshooter.game.event.AddPoints
import pl.klolo.spaceshooter.game.event.Collision
import pl.klolo.spaceshooter.game.event.DisableDoublePoints
import pl.klolo.spaceshooter.game.event.EnableDoublePoints
import pl.klolo.spaceshooter.game.event.EnemyDestroyed
import pl.klolo.spaceshooter.game.event.EnemyOutOfScreen
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.RegisterEntity


class EnemyLogic(
    private val entityRegistry: EntityRegistry,
    private val gamePhysics: GamePhysics,
    private val eventBus: EventBus,
    private val gameLighting: GameLighting
) : EntityLogic<SpriteEntityWithLogic> {

    private val explosionConfiguration = entityRegistry.getConfigurationById("explosion")
    private var explosionLights = ExplosionEffect(gameLighting, 50f)
    private var popupMessages: PopupMessages = PopupMessages(entityRegistry, eventBus)

    private var explosion: ParticleEntity? = null
    private var light: PointLight? = null

    private lateinit var physicsShape: CircleShape
    private lateinit var body: Body
    private var life: Int = 0
    private var doublePoints = false
    private val lifeFactory = 20

    var shootDelay = 3f
    var speed = 1f
    private val lightDistance = 40f
    private val lightDistanceDistanceAfterExplosion = 500f
    private val SpriteEntityWithLogic.isAboveScreen: Boolean
        get() = y > getScreenHeight()

    override val onDispose: SpriteEntityWithLogic.() -> Unit = {
        if (display) {
            physicsShape.dispose()
            gamePhysics.destroy(body)
        }
    }


    override val initialize: SpriteEntityWithLogic.() -> Unit = {
        val laserConfiguration = entityRegistry.getConfigurationById("laserRed01")
        light = gameLighting.createPointLight(50, Colors.redLight, lightDistance, x, y)

        life = uniqueName
            .elementAt(uniqueName.lastIndex)
            .toString()
            .toInt() * lifeFactory

        createPhysics()

        addSequence(
            moveTo(x, -1 * height, speed),
            execute { onDestroyBecauseOutScreen() }
        )

        addForeverSequence(
            delay(shootDelay),
            execute { shootOnPosition(laserConfiguration) }
        )

        eventBus
            .subscribe(id)
            .onEvent<Collision> { onCollision(it) }
            .onEvent<EnableDoublePoints> { doublePoints = true }
            .onEvent<DisableDoublePoints> { doublePoints = false }
    }

    private fun SpriteEntityWithLogic.onCollision(it: Collision) {
        if (isAboveScreen) {
            return
        }

        val collidedEntity = it.entity!! as SpriteEntityWithLogic
        if (isPlayerLaser(collidedEntity) && display) {
            life -= (collidedEntity.logic as BulletLogic).bulletPower

            if (life <= 0) {
                onDestroyEnemy()
            } else {
                explosionLights.addLight(this)
            }
        }
    }

    private fun SpriteEntityWithLogic.shootOnPosition(laserConfiguration: EntityConfiguration) {
        if (isAboveScreen) {
            return
        }

        val bulletXPosition = x + width / 2 // width of the enemy
        val bulletYPosition = y - height / 2

        val bulletEntity: SpriteEntityWithLogic = createEntity(laserConfiguration, false) {
            x = bulletXPosition - width / 2 // width of the bullet
            y = bulletYPosition

        } as SpriteEntityWithLogic

        val bulletLogic = bulletEntity.logic as BulletLogic
        bulletLogic.direction = Direction.DOWN
        bulletLogic.lightColor = Colors.redLightAccent
        bulletLogic.apply {
            initialize.invoke(bulletEntity)
        }
        eventBus.sendEvent(RegisterEntity(bulletEntity))
    }

    private fun SpriteEntityWithLogic.onDestroyEnemy() {
        clearActions()
        showExplosion()
        showPopup()

        executeAfterDelay(0.2f) { eventBus.sendEvent(EnemyDestroyed(x, y)) }

        onDispose()
        display = false
        eventBus.sendEvent(AddPoints(height.toInt()))
    }

    private fun SpriteEntityWithLogic.onDestroyBecauseOutScreen() {
        clearActions()
        display = false
        shouldBeRemove = true
        eventBus.sendEvent(EnemyOutOfScreen())
    }

    private fun SpriteEntityWithLogic.showPopup() {
        val popupMessageConfiguration = PopupMessageConfiguration(
            message = "+${if (doublePoints) height.toInt() * 2 else height.toInt()}",
            callback = {
                shouldBeRemove = true
                light?.remove()
            }
        )
        popupMessages.show(this, popupMessageConfiguration)
    }

    private fun SpriteEntityWithLogic.showExplosion() {
        explosion = createEntity(explosionConfiguration)
        val currentX = x
        val currentY = y
        explosion.apply {
            this!!.effect.setPosition(currentX, currentY)
        }
        eventBus.sendEvent(RegisterEntity(explosion))
        light?.color = Colors.redLight
        val explosionLightFadeOutTime = 35f
        addAction(fadeOut(explosionLightFadeOutTime))
    }

    override val onUpdate: SpriteEntityWithLogic.(Float) -> Unit = {
        light?.setPosition(x + width / 2, y + height / 2)
        body.setTransform(x + width / 2, y + height / 2, 0.0f)
        explosionLights.updateLight()

        if (!display) {
            updateLightAfterDestroyedEnemy()
        }

        updateExplosion()
    }

    private fun SpriteEntityWithLogic.updateLightAfterDestroyedEnemy() {
        light?.distance = lightDistanceDistanceAfterExplosion * color.a
        if ((light?.distance ?: 1f) < 0.01) {
            explosionLights.onDispose()
            light?.isActive = false
            light = null
        }
    }

    private fun SpriteEntityWithLogic.updateExplosion() {
        val currentX = x
        val currentY = y
        explosion?.apply { this.effect.setPosition(currentX, currentY) }
    }

    private fun SpriteEntityWithLogic.createPhysics() {
        body = gamePhysics.createDynamicBody()
        physicsShape = CircleShape().apply { radius = width / 2 }
        val fixture = body.createFixture(gamePhysics.getStandardFixtureDef(physicsShape))
        fixture?.userData = this
    }

}