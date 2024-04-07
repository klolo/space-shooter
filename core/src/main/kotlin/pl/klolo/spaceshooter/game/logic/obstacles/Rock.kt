package pl.klolo.spaceshooter.game.logic.obstacles

import box2dLight.PointLight
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo
import com.badlogic.gdx.utils.Align
import pl.klolo.spaceshooter.game.engine.physics.GameLighting
import pl.klolo.spaceshooter.game.common.addSequence
import pl.klolo.spaceshooter.game.common.execute
import pl.klolo.spaceshooter.game.common.getScreenHeight
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.entity.isPlayerLaser
import pl.klolo.spaceshooter.game.engine.entity.kind.ParticleEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics
import pl.klolo.spaceshooter.game.logic.Bullet
import pl.klolo.spaceshooter.game.logic.Collision
import pl.klolo.spaceshooter.game.logic.EnemyOutOfScreen
import pl.klolo.spaceshooter.game.logic.RegisterEntity
import pl.klolo.spaceshooter.game.logic.enemy.ExplosionEffect

@Suppress("unused")
open class Rock(
    private val entityRegistry: EntityRegistry,
    private val gamePhysics: GamePhysics,
    private val eventBus: EventBus,
    private val gameLighting: GameLighting,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : AbstractObstacle(gameLighting, entityConfiguration, sprite) {

    protected val explosionConfiguration = entityRegistry.getConfigurationById("explosion")

    protected var explosionLights = ExplosionEffect(gameLighting, 90f, Color.WHITE)

    protected var explosion: ParticleEntity? = null

    protected var light: PointLight? = null

    protected lateinit var physicsShape: CircleShape

    protected lateinit var body: Body

    protected var life: Int = 0

    protected val isAboveScreen: Boolean
        get() = y > getScreenHeight()

    override fun onDispose() {
        if (display) {
            physicsShape.dispose()
            gamePhysics.destroy(body)
        }
    }

    override fun onInitialize() {
        setOrigin(Align.center);
        light = gameLighting.createPointLight(50, Color.WHITE, 50 + width, x, y)
        life = 800

        createPhysics()

        addSequence(
            moveTo(x, -1 * height, speed),
            execute {
                onDestroyEnemy()
                eventBus.sendEvent(EnemyOutOfScreen())
            }
        )

        addAction(
            forever(
                Actions.rotateTo(3600f, 60f)
            )
        )

        eventBus
            .subscribe(id)
            .onEvent<Collision> { onCollision(it) }
    }

    private fun onCollision(it: Collision) {
        if (isAboveScreen) {
            return
        }

        val collidedEntity = it.entity!! as SpriteEntity

        if (isPlayerLaser(collidedEntity) && display) {
            life -= (collidedEntity as Bullet).bulletPower
            collidedEntity.shouldBeRemove = true

            if (life <= 0) {
                onDestroyEnemy()
                showExplosion()
            }
            else {
                explosionLights.addLight(this)
            }
        }
    }

    open fun onDestroyEnemy() {
        clearActions()
        onDispose()
        display = false

        light?.isActive = false
        light?.remove()
    }

    private fun showExplosion() {
        explosion = createEntity(explosionConfiguration)
        val currentX = x
        val currentY = y
        explosion.apply {
            this!!.effect.setPosition(currentX, currentY)
        }
        eventBus.sendEvent(RegisterEntity(explosion))
        light?.color = Color.GRAY
        val explosionLightFadeOutTime = 35f

        addSequence(
            fadeOut(explosionLightFadeOutTime),
            execute { explosionLights.onDispose() }
        )
    }

    override fun onUpdate(delta: Float) {
        body.setTransform(x + width / 2, y + height / 2, body.angle)
        light?.setPosition(x + width / 2, y + height / 2)

        explosionLights.updateLight()

        updateExplosion()
        super.onUpdate(delta)
    }

    private fun updateExplosion() {
        explosion?.apply { this.effect.setPosition(this@Rock.x, this@Rock.y) }
    }

    private fun createPhysics() {
        body = gamePhysics.createDynamicBody()
        physicsShape = CircleShape().apply { radius = width / 2 }
        val fixture = body.createFixture(gamePhysics.getStandardFixtureDef(physicsShape))
        fixture?.userData = this
    }

}