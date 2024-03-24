package pl.klolo.spaceshooter.game.logic.bonus

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.BossCreated
import pl.klolo.spaceshooter.game.logic.BossDestroyed
import pl.klolo.spaceshooter.game.logic.RegisterEntity
import java.util.Random


class BonusGenerator(
    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry,
    entityConfiguration: EntityConfiguration
) : ActorEntity(entityConfiguration) {

    private val random = Random()

    private var bossActive = false

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

    override fun onInitialize() {
        Gdx.app.debug(this.javaClass.name, "BonusGenerator initialize")

        addAction(
            forever(
                sequence(
                    Actions.run {
                        if (bossActive) {
                            return@run
                        }

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

    private fun getRandomItemConfiguration(): Pair<EntityConfiguration, Int> {
        return items[random.nextInt(items.size)]
    }

    private fun shouldCreateItem(createItemPropability: Int): Boolean {
        return random.nextInt(100) % createItemPropability == 0
    }

    fun createItem(bonusItemConfiguration: EntityConfiguration): SpriteEntity {
        val random = Random()
        val margin = 100

        val enemyXPosition = random.nextInt(Gdx.graphics.width.toFloat().toInt() - margin)
        val enemyYPosition = Gdx.graphics.height.toFloat() + margin

        val enemyEntity = createEntity<SpriteEntity>(bonusItemConfiguration)
        enemyEntity.apply {
            x = enemyXPosition.toFloat() + width
            y = enemyYPosition
        }

        enemyEntity.onInitialize()
        return enemyEntity
    }
}
