package pl.klolo.game.logic

import box2dLight.PointLight
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo
import pl.klolo.game.common.Colors.blueLight
import pl.klolo.game.event.*
import pl.klolo.game.common.addSequence
import pl.klolo.game.common.execute
import pl.klolo.game.common.executeAfterDelay
import pl.klolo.game.entity.*
import pl.klolo.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.game.physics.GameLighting
import pl.klolo.game.physics.GamePhysics
import java.util.*

enum class Direction { DOWN, UP }

class BulletLogic(
        private val gamePhysics: GamePhysics,
        private val eventProcessor: EventProcessor,
        private val gameLighting: GameLighting) : EntityLogic<SpriteEntityWithLogic> {

    lateinit var bulletLight: PointLight
    private lateinit var physicsShape: PolygonShape
    var isEnemyBullet = true
    private lateinit var body: Body
    var lightColor = blueLight
    var direction: Direction = Direction.UP
    var bulletPower = 10

    override val onDispose: SpriteEntityWithLogic.() -> Unit = {
        physicsShape.dispose()
        gamePhysics.destroy(body)
        bulletLight.remove()
    }

    override val initialize: SpriteEntityWithLogic.() -> Unit = {
        bulletLight = gameLighting.createPointLight(100, lightColor, 50f, x, y)

        createPhysics()

        addSequence(
                moveTo(x, getTargetYPosition(), 3f + Random().nextFloat()),
                execute { shouldBeRemove = true }
        )

        executeAfterDelay(0.25f) {
            eventProcessor
                    .subscribe(id)
                    .onEvent<Collision> {
                        onCollision(it)
                    }
        }
    }

    private fun SpriteEntityWithLogic.onCollision(it: Collision) {
        val collidedEntity = it.entity

        if (collidedEntity != null && !shouldBeRemove) {
            val enemyHitPlayer = isEnemyBullet && isPlayerByName(collidedEntity)
            val playerHitEnemy = !isEnemyBullet && isEnemyByName(collidedEntity)
            val enemyHitShield = isEnemyBullet && isShieldByName(collidedEntity)
            val hitBonus = isExtraBonus(collidedEntity)

            shouldBeRemove = (enemyHitPlayer || playerHitEnemy || enemyHitShield) && !hitBonus

            if (enemyHitShield) {
                eventProcessor.sendEvent(LaserHitInShield(it.x, it.y))
            }
        }
    }

    private fun SpriteEntityWithLogic.getTargetYPosition(): Float {
        return when (direction) {
            Direction.UP -> y + Gdx.graphics.height.toFloat()
            Direction.DOWN -> y - Gdx.graphics.height.toFloat()
        }
    }

    override val onUpdate: SpriteEntityWithLogic.(Float) -> Unit = {
        bulletLight.setPosition(x + width / 2, y + height / 2)
        body.setTransform(x + width / 2, y + height / 2, 0.0f)

        val minYPositionOffset = 100
        if (y + minYPositionOffset < 0) {
            shouldBeRemove = true
        }
    }

    private fun SpriteEntityWithLogic.createPhysics() {
        physicsShape = PolygonShape().apply { setAsBox(width / 2, height / 2) }
        body = gamePhysics.createDynamicBody()

        val fixture = body.createFixture(gamePhysics.getStandardFixtureDef(physicsShape))
        fixture.userData = this
    }
}