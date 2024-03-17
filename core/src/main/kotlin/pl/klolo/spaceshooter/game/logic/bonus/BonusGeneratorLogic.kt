package pl.klolo.spaceshooter.game.logic.bonus

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.RegisterEntity
import pl.klolo.spaceshooter.game.entity.EntityLogic
import pl.klolo.spaceshooter.game.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.entity.EntityRegistry
import pl.klolo.spaceshooter.game.entity.createEntity
import pl.klolo.spaceshooter.game.entity.kind.EntityWithLogic
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import java.util.*

fun createItem(bonusItemConfiguration: EntityConfiguration): SpriteEntityWithLogic {
    val random = Random()
    val margin = 100

    val enemyXPosition = random.nextInt(Gdx.graphics.width.toFloat().toInt() - margin)
    val enemyYPosition = Gdx.graphics.height.toFloat() + margin

    val enemyEntity: SpriteEntityWithLogic = createEntity(bonusItemConfiguration, false) {
        x = enemyXPosition.toFloat() + width
        y = enemyYPosition
    } as SpriteEntityWithLogic

    enemyEntity.logic.apply { initialize.invoke(enemyEntity) }
    return enemyEntity
}

class BonusGeneratorLogic(
    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry
) : EntityLogic<EntityWithLogic> {

    private val random = Random()

    private val items by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        listOf(
            entityRegistry.getConfigurationById("medicineBonus") to 40,
            entityRegistry.getConfigurationById("goldStarBonus") to 40,
            entityRegistry.getConfigurationById("superBulletBonus") to 40,
            entityRegistry.getConfigurationById("shieldBonus") to 40,
            entityRegistry.getConfigurationById("doublePointsBonus") to 40,
            entityRegistry.getConfigurationById("magnetPointsBonus") to 40
        )
    }

    override val initialize: EntityWithLogic.() -> Unit = {
        Gdx.app.debug(this.javaClass.name, "BonusGeneratorLogic initialize")

        addAction(
            forever(
                sequence(
                    Actions.run {
                        val randomItem = getRandomItemConfiguration()
                        if (shouldCreateItem(randomItem.second)) {
                            val enemyEntity = createItem(randomItem.first)
                            eventBus.sendEvent(RegisterEntity(enemyEntity))
                        }
                    },
                    delay(1f)
                )
            )
        )
    }

    override val onDispose: EntityWithLogic.() -> Unit = {
    }

    override val onUpdate: EntityWithLogic.(Float) -> Unit = {
    }

    private fun getRandomItemConfiguration(): Pair<EntityConfiguration, Int> {
        return items[random.nextInt(items.size)]
    }

    private fun shouldCreateItem(createItemPropability: Int): Boolean {
        return random.nextInt(100) % createItemPropability == 0
    }
}
