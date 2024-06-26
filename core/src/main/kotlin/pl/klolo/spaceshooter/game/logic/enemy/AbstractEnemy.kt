package pl.klolo.spaceshooter.game.logic.enemy

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Action
import pl.klolo.spaceshooter.game.common.getScreenHeight
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity

abstract class AbstractEnemy(
    entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : SpriteEntity(entityConfiguration, sprite) {

    lateinit var moveAction: Action

    var shootDelay = 8f

    var speed = 0.8f

    protected fun isAboveScreen(y: Float) = y > getScreenHeight()

}