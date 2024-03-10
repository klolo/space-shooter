package pl.klolo.spaceshooter.game.logic.bonus

import pl.klolo.spaceshooter.game.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.entity.EntityLogic
import pl.klolo.spaceshooter.game.entity.EntityRegistry
import pl.klolo.spaceshooter.game.entity.createEntity
import pl.klolo.spaceshooter.game.entity.kind.EntityWithLogic
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.event.EnemyDestroyed
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.RegisterEntity

class ExtraStarBonusGenerator(
    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry
) : EntityLogic<EntityWithLogic> {

    private lateinit var starConfiguration: EntityConfiguration

    override val initialize: EntityWithLogic.() -> Unit = {
        starConfiguration = entityRegistry.getConfigurationById("starBonus")

        eventBus
                .subscribe(id)
                .onEvent<EnemyDestroyed> {
                    val entity: SpriteEntityWithLogic = createEntityOnPosition(it.x, it.y)
                    entity.logic.apply { initialize.invoke(entity) }
                    eventBus.sendEvent(RegisterEntity(entity))
                }
    }

    private fun createEntityOnPosition(enemyXPosition: Float, enemyYPosition: Float): SpriteEntityWithLogic {
        return createEntity(starConfiguration, false) {
            x = enemyXPosition
            y = enemyYPosition
        } as SpriteEntityWithLogic
    }

    override val onUpdate: EntityWithLogic.(Float) -> Unit = {

    }

    override val onDispose: EntityWithLogic.() -> Unit = {

    }
}