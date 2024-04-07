package pl.klolo.spaceshooter.game.logic.obstacles

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Action
import pl.klolo.spaceshooter.game.engine.physics.GameLighting
import pl.klolo.spaceshooter.game.common.getScreenHeight
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity

abstract class AbstractObstacle(
    private val gameLighting: GameLighting,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : SpriteEntity(entityConfiguration, sprite) {

    lateinit var moveAction: Action

    var speed = 1.5f

    protected fun isAboveScreen(y: Float) = y > getScreenHeight()

}