package pl.klolo.spaceshooter.game.logic.enemy

import box2dLight.Light
import com.badlogic.gdx.graphics.Color
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.common.Colors.redLight
import pl.klolo.spaceshooter.game.engine.physics.GameLighting
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import java.util.*

const val explosionLightLifeTime = 0.15f

class ExplosionEffect(
    private val gameLighting: GameLighting,
    private val distance: Float,
    private val lightColor: Color = redLight
) {

    private val hitLights = Stack<Light>()

    var addLight: SpriteEntity.() -> Unit = {
        hitLights.push(
            gameLighting.createPointLight(
                150,
                lightColor,
                distance,
                x + width / 2,
                y + height / 2
            )
        )
        executeAfterDelay(explosionLightLifeTime) { hitLights.pop()?.remove() }
    }

    var addLightOnPosition: SpriteEntity.(posX: Float, posY: Float) -> Unit = { posX, posY ->
        hitLights.push(gameLighting.createPointLight(150, lightColor, distance, posX, posY))
        executeAfterDelay(explosionLightLifeTime) { hitLights.pop()?.remove() }
    }

    fun onDispose() {
        hitLights.forEach(Light::remove)
    }

    fun updateLight() {
        hitLights.forEach { it.distance *= 0.9f }
    }
}