package pl.klolo.spaceshooter.game.logic.enemy

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Action
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.common.getScreenHeight
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics

abstract class AbstractEnemy(
    private val entityRegistry: EntityRegistry,
    private val gamePhysics: GamePhysics,
    private val eventBus: EventBus,
    private val gameLighting: GameLighting,
    entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : SpriteEntity(entityConfiguration, sprite) {

    lateinit var moveAction: Action

    var shootDelay = 3f

    var speed = 1f

    protected fun isAboveScreen(y: Float) = y > getScreenHeight()

}