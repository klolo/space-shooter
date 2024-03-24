package pl.klolo.spaceshooter.game.logic

import box2dLight.PointLight
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.common.Colors.blueLight
import pl.klolo.spaceshooter.game.common.addSequence
import pl.klolo.spaceshooter.game.common.execute
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.isEnemyByName
import pl.klolo.spaceshooter.game.engine.entity.isExtraBonus
import pl.klolo.spaceshooter.game.engine.entity.isPlayerByName
import pl.klolo.spaceshooter.game.engine.entity.isShieldByName
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics
import java.util.Random

enum class Direction { DOWN, UP }

class Bullet(
    private val gamePhysics: GamePhysics,
    private val eventBus: EventBus,
    private val gameLighting: GameLighting,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : SpriteEntity(entityConfiguration, sprite) {

    lateinit var bulletLight: PointLight
    lateinit var physicsShape: PolygonShape
    var isEnemyBullet = true
    private lateinit var body: Body
    var lightColor = blueLight
    var direction: Direction = Direction.UP
    var bulletPower = 10


    override fun onInitialize() {
        bulletLight = gameLighting.createPointLight(100, lightColor, 50f, x, y)

        createPhysics()

        addSequence(
            moveTo(x, getTargetYPosition(), 3f + Random().nextFloat()),
            execute { shouldBeRemove = true }
        )

        executeAfterDelay(0.25f) {
            eventBus
                .subscribe(id)
                .onEvent<Collision> {
                    onCollision(it)
                }
        }
    }

    override fun onDispose() {
        physicsShape.dispose()
        gamePhysics.destroy(body)
        bulletLight.remove()
    }

    private fun onCollision(it: Collision) {
        val collidedEntity = it.entity

        if (collidedEntity != null && !shouldBeRemove) {
            val enemyHitPlayer = isEnemyBullet && isPlayerByName(collidedEntity)
            val playerHitEnemy = !isEnemyBullet && isEnemyByName(collidedEntity)
            val enemyHitShield = isEnemyBullet && isShieldByName(collidedEntity)
            val hitBonus = isExtraBonus(collidedEntity)

            shouldBeRemove = (enemyHitPlayer || playerHitEnemy || enemyHitShield) && !hitBonus

            if (enemyHitShield) {
                eventBus.sendEvent(LaserHitInShield(it.x, it.y))
            }
        }
    }

    private fun getTargetYPosition(): Float {
        return when (direction) {
            Direction.UP -> y + Gdx.graphics.height.toFloat()
            Direction.DOWN -> y - Gdx.graphics.height.toFloat()
        }
    }

    override fun onUpdate(delta: Float) {
        bulletLight.setPosition(x + width / 2, y + height / 2)
        body.setTransform(x + width / 2, y + height / 2, 0.0f)

        val minYPositionOffset = 100
        if (y + minYPositionOffset < 0) {
            shouldBeRemove = true
        }

        super.onUpdate(delta)
    }

    private fun createPhysics() {
        physicsShape = PolygonShape().apply { setAsBox(width / 2, height / 2) }
        body = gamePhysics.createDynamicBody()

        val fixture = body.createFixture(gamePhysics.getStandardFixtureDef(physicsShape))
        fixture.userData = this
    }
}