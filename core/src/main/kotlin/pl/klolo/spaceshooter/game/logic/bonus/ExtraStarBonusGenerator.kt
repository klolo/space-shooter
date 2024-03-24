package pl.klolo.spaceshooter.game.logic.bonus

import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.logic.EnemyDestroyed
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.RegisterEntity

class ExtraStarBonusGenerator(
    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry,
    entityConfiguration: EntityConfiguration
) : ActorEntity(entityConfiguration) {

    private lateinit var starConfiguration: EntityConfiguration

    override fun onInitialize() {
        starConfiguration = entityRegistry.getConfigurationById("silverStarBonus")

        eventBus
            .subscribe(id)
            .onEvent<EnemyDestroyed> {
                val entity: SpriteEntity = createEntityOnPosition(it.x, it.y)
                entity.onInitialize()
                eventBus.sendEvent(RegisterEntity(entity))
            }
    }

    private fun createEntityOnPosition(enemyXPosition: Float, enemyYPosition: Float): SpriteEntity {
        return createEntity<SpriteEntity>(starConfiguration).apply {
            x = enemyXPosition
            y = enemyYPosition
        }
    }

}