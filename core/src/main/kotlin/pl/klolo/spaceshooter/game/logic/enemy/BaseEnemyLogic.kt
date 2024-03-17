package pl.klolo.spaceshooter.game.logic.enemy

import com.badlogic.gdx.scenes.scene2d.Action
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.common.getScreenHeight
import pl.klolo.spaceshooter.game.entity.EntityLogic
import pl.klolo.spaceshooter.game.entity.EntityRegistry
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.physics.GamePhysics

abstract class BaseEnemyLogic(
    private val entityRegistry: EntityRegistry,
    private val gamePhysics: GamePhysics,
    private val eventBus: EventBus,
    private val gameLighting: GameLighting
) : EntityLogic<SpriteEntityWithLogic> {

    lateinit var moveAction: Action
    
    var shootDelay = 3f

    var speed = 1f

    protected fun isAboveScreen(y: Float) = y > getScreenHeight()

}