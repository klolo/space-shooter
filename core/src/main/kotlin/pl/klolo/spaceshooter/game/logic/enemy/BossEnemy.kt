package pl.klolo.spaceshooter.game.logic.enemy

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo
import pl.klolo.spaceshooter.game.engine.physics.GameLighting
import pl.klolo.spaceshooter.game.common.Colors
import pl.klolo.spaceshooter.game.common.addForeverSequence
import pl.klolo.spaceshooter.game.common.addSequence
import pl.klolo.spaceshooter.game.common.execute
import pl.klolo.spaceshooter.game.common.getScreenHeight
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics
import pl.klolo.spaceshooter.game.logic.BossCreated
import pl.klolo.spaceshooter.game.logic.BossDestroyed
import pl.klolo.spaceshooter.game.logic.Bullet
import pl.klolo.spaceshooter.game.logic.Collision
import pl.klolo.spaceshooter.game.logic.Direction
import pl.klolo.spaceshooter.game.logic.PlayerChangePosition
import pl.klolo.spaceshooter.game.logic.RegisterEntity

@Suppress("unused")
class BossEnemy(
    private val entityRegistry: EntityRegistry,
    private val gamePhysics: GamePhysics,
    private val eventBus: EventBus,
    val gameLighting: GameLighting,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : Enemy(entityRegistry, gamePhysics, eventBus, gameLighting, entityConfiguration, sprite) {

    override fun onDispose() {
        if (display) {
            physicsShape.dispose()
            gamePhysics.destroy(body)
        }
    }

    override fun onInitialize() {
        lifeFactory = 1000
        shootDelay = 0.7f
        lightDistance = 250f
        speed = 0.4f

        val laserConfiguration = entityRegistry.getConfigurationById("laserRed01")
        light = gameLighting.createPointLight(150, Colors.redLightAccent, lightDistance, x, y)
        life = uniqueName
            .elementAt(uniqueName.lastIndex)
            .toString()
            .toInt() * lifeFactory

        createPhysics()

        addForeverSequence(
            delay(shootDelay),
            execute { shootOnPosition(laserConfiguration) }
        )

        eventBus
            .subscribe(id)
            .onEvent<Collision> { onCollision(it) }

        eventBus.sendEvent(BossCreated())

        moveAction = addSequence(
            moveTo(x, getScreenHeight() / 2, speed)
        )

        addForeverSequence(
            delay(shootDelay),
            execute { shootOnPosition(laserConfiguration) }
        )

        eventBus.subscribe(id)
            .onEvent<PlayerChangePosition> {
                removeAction(moveAction)
                moveAction = addSequence(
                    moveTo(it.x - width / 2, getScreenHeight() * 0.65f, speed)
                )
            }
    }

    override fun onDestroyEnemy() {
        super.onDestroyEnemy()
        eventBus.sendEvent(BossDestroyed())
    }

    override fun shootOnPosition(laserConfiguration: EntityConfiguration) {
        if (isAboveScreen) {
            return
        }

        val bulletXPosition = x + width / 2 // width of the enemy
        val bulletYPosition = y + (height / 2) - 180

        val bulletEntity: SpriteEntity = createEntity<SpriteEntity>(laserConfiguration).apply {
            x = bulletXPosition
            y = bulletYPosition
        }

        val bullet = bulletEntity as Bullet
        bullet.direction = Direction.DOWN
        bullet.lightColor = Colors.redLightAccent
        bullet.onInitialize()
        eventBus.sendEvent(RegisterEntity(bulletEntity))
    }

}