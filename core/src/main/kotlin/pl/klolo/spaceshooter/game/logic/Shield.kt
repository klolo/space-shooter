package pl.klolo.spaceshooter.game.logic

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.CircleShape
import pl.klolo.spaceshooter.game.engine.physics.GameLighting
import pl.klolo.spaceshooter.game.common.Colors.blueLight
import pl.klolo.spaceshooter.game.engine.SoundEffect
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.enemy.ExplosionEffect
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics

class Shield(
    private val gamePhysics: GamePhysics,
    private val eventBus: EventBus,
    private val gameLighting: GameLighting,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : SpriteEntity(entityConfiguration, sprite) {

    private var explosionLights = ExplosionEffect(gameLighting, 200f, blueLight)
    private lateinit var physicsShape: CircleShape
    private lateinit var body: Body

    override fun onInitialize() {
        useLighting = false

        eventBus
            .subscribe(id)
            .onEvent<PlayerChangePosition> {
                x = it.x - width / 2
                y = it.y - height / 2
            }
            .onEvent<DisableShield> {
                Gdx.app.debug(this.javaClass.name, "disabling shield...")
                body.isActive = false
                display = false
            }
            .onEvent<EnableShield> {
                Gdx.app.debug(this.javaClass.name, "enabling shield...")
                body.isActive = true
                display = true
            }
            .onEvent<LaserHitInShield> {
                if (!body.isActive) {
                    return@onEvent
                }

                eventBus.sendEvent(PlaySound(SoundEffect.SHIELD_COLLISION))
                explosionLights.addLightOnPosition(this, it.x, it.y)
            }

        createShieldPhysics()
    }

    override fun onUpdate(delta: Float) {
        body.setTransform(x + width / 2, y + height / 2, 0.0f)
        explosionLights.updateLight()
    }

    override fun onDispose() {
        physicsShape.dispose()
        gamePhysics.destroy(body)
        explosionLights.onDispose()
    }

    private fun SpriteEntity.createShieldPhysics() {
        body = gamePhysics.createDynamicBody()
        body.isActive = false

        physicsShape = CircleShape().apply { radius = width / 2 }
        display = false

        val fixture = body.createFixture(gamePhysics.getStandardFixtureDef(physicsShape))
        fixture.userData = this
    }
}