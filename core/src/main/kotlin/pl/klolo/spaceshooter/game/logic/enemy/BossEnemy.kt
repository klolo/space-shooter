package pl.klolo.spaceshooter.game.logic.enemy

import box2dLight.PointLight
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.engine.entity.isPlayerLaser
import pl.klolo.spaceshooter.game.engine.entity.kind.ParticleEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.logic.Bullet
import pl.klolo.spaceshooter.game.logic.Direction
import pl.klolo.spaceshooter.game.logic.helper.PopupMessageConfiguration
import pl.klolo.spaceshooter.game.logic.helper.PopupMessages
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics
import pl.klolo.spaceshooter.game.common.Colors
import pl.klolo.spaceshooter.game.common.addForeverSequence
import pl.klolo.spaceshooter.game.common.addSequence
import pl.klolo.spaceshooter.game.common.execute
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.common.getScreenHeight
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.logic.AddPoints
import pl.klolo.spaceshooter.game.logic.Collision
import pl.klolo.spaceshooter.game.logic.DisableDoublePoints
import pl.klolo.spaceshooter.game.logic.EnableDoublePoints
import pl.klolo.spaceshooter.game.logic.EnemyDestroyed
import pl.klolo.spaceshooter.game.logic.EnemyOutOfScreen
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.RegisterEntity

class BossEnemy(
    private val entityRegistry: EntityRegistry,
    private val gamePhysics: GamePhysics,
    private val eventBus: EventBus,
    private val gameLighting: GameLighting,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : AbstractEnemy(entityRegistry, gamePhysics, eventBus, gameLighting, entityConfiguration, sprite) {

    private val explosionConfiguration = entityRegistry.getConfigurationById("explosion")
    private var explosionLights = ExplosionEffect(gameLighting, 50f)
    private var popupMessages: PopupMessages = PopupMessages(entityRegistry, eventBus)

    private var explosion: ParticleEntity? = null
    private var light: PointLight? = null

    private lateinit var physicsShape: CircleShape
    private lateinit var body: Body
    private var life: Int = 0
    private var doublePoints = false
    private val lifeFactory = 50_000

    private val lightDistance = 40f
    private val lightDistanceDistanceAfterExplosion = 500f

    override fun onDispose() {
        if (display) {
            physicsShape.dispose()
            gamePhysics.destroy(body)
        }
    }

    override fun onInitialize() {
        val laserConfiguration = entityRegistry.getConfigurationById("laserRed01")
        light = gameLighting.createPointLight(50, Colors.redLight, lightDistance, x, y)
        life = uniqueName
            .elementAt(uniqueName.lastIndex)
            .toString()
            .toInt() * lifeFactory

        createPhysics()

        shootDelay = 2f

        moveAction = addSequence(
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

    private fun onCollision(it: Collision) {
        if (isAboveScreen(y)) {
            return
        }

        val collidedEntity = it.entity!! as SpriteEntity
        if (isPlayerLaser(collidedEntity) && display) {
            life -= (collidedEntity as Bullet).bulletPower

            if (life <= 0) {
                onDestroyEnemy()
            } else {
                explosionLights.addLight(this)
            }
        }
    }

    private fun shootOnPosition(laserConfiguration: EntityConfiguration) {
        if (isAboveScreen(y)) {
            return
        }

        val bulletXPosition = x + width / 2 // width of the enemy
        val bulletYPosition = y - height / 2

        val bulletEntity: SpriteEntity = createEntity<SpriteEntity>(laserConfiguration).apply {
            x = bulletXPosition - width / 2 // width of the bullet
            y = bulletYPosition
        }

        val bullet = bulletEntity as Bullet
        bullet.direction = Direction.DOWN
        bullet.lightColor = Colors.redLightAccent
        bullet.onInitialize()
        eventBus.sendEvent(RegisterEntity(bulletEntity))
    }

    private fun onDestroyEnemy() {
        clearActions()
        showExplosion()
        showPopup()

        executeAfterDelay(0.2f) { eventBus.sendEvent(EnemyDestroyed(x, y)) }

        onDispose()
        display = false
        eventBus.sendEvent(AddPoints(height.toInt()))
    }

    private fun onDestroyBecauseOutScreen() {
        clearActions()
        display = false
        shouldBeRemove = true
        eventBus.sendEvent(EnemyOutOfScreen())
    }

    private fun showPopup() {
        val popupMessageConfiguration = PopupMessageConfiguration(
            message = "+${if (doublePoints) height.toInt() * 2 else height.toInt()}",
            callback = {
                shouldBeRemove = true
                light?.remove()
            }
        )
        popupMessages.show(this, popupMessageConfiguration)
    }

    private fun showExplosion() {
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

    fun updateExplosion(currentX: Float, currentY: Float) {
        explosion?.apply { this.effect.setPosition(currentX, currentY) }
    }

    override fun onUpdate(delta: Float)  {
        light?.setPosition(x + width / 2, y + height / 2)
        body.setTransform(x + width / 2, y + height / 2, 0.0f)
        explosionLights.updateLight()

        if (!display) {
            updateLightAfterDestroyedEnemy()
        }

        if (y < getScreenHeight() / 2) {
            y = getScreenHeight() / 2
            removeAction(moveAction)
        }

        updateExplosion(x, y)
        super.onUpdate(delta)
    }

    private fun updateLightAfterDestroyedEnemy() {
        light?.distance = lightDistanceDistanceAfterExplosion * color.a
        if ((light?.distance ?: 1f) < 0.01) {
            explosionLights.onDispose()
            light?.isActive = false
            light = null
        }
    }

    private fun createPhysics() {
        body = gamePhysics.createDynamicBody()
        physicsShape = CircleShape().apply { radius = width / 2 }
        val fixture = body.createFixture(gamePhysics.getStandardFixtureDef(physicsShape))
        fixture?.userData = this
    }

}