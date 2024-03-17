package pl.klolo.spaceshooter.game.logic.enemy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import pl.klolo.spaceshooter.game.engine.GameEngine
import pl.klolo.spaceshooter.game.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.entity.EntityLogic
import pl.klolo.spaceshooter.game.entity.EntityRegistry
import pl.klolo.spaceshooter.game.entity.createEntity
import pl.klolo.spaceshooter.game.entity.kind.EntityWithLogic
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.event.EnemyDestroyed
import pl.klolo.spaceshooter.game.event.EnemyOutOfScreen
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.RegisterEntity
import java.util.Random
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

const val speedOfTheDecreasingEnemyShootDelayPerCreatedEnemy = 500f
const val minimalShootDelay = 0.5f

class EnemyGeneratorLogic(    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry
) : EntityLogic<EntityWithLogic> {

    private var enemySpeed = 1f
    private var maxEnemiesOnStage = 3
    private var enemiesCount = AtomicInteger(0)
    private var totalCreatedEnemy = 0
    private val random = Random()

    override val onDispose: EntityWithLogic.() -> Unit = {

    }

    override val initialize: EntityWithLogic.() -> Unit = {
        enemySpeed = GameEngine.applicationConfiguration.getConfig("engine")
            .getDouble("enemySpeed")
            .toFloat()

        Gdx.app.debug(this.javaClass.name, "createSubscription")
        eventBus
            .subscribe(id)
            .onEvent<EnemyDestroyed> {
                printEnemiesStatistic()
                enemiesCount.decrementAndGet()
            }
            .onEvent<EnemyOutOfScreen> {
                printEnemiesStatistic()
                enemiesCount.decrementAndGet()
            }

        addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.run {
                        if (enemiesCount.get() < maxEnemiesOnStage) {
                            val laserConfiguration = entityRegistry.getConfigurationById(
                                "enemyRed" + (1 + random.nextInt(5))
                            )
                            enemiesCount.incrementAndGet()
                            totalCreatedEnemy++
                            createEnemy(laserConfiguration)

                            maxEnemiesOnStage =
                                max(Math.floorDiv(totalCreatedEnemy, 10), maxEnemiesOnStage)
                        }
                    },
                    Actions.delay(2f / random.nextInt(4)) // FIXME: Random.nextFloat doesnt work
                )
            )
        )
    }

    private fun EntityWithLogic.createEnemy(laserConfiguration: EntityConfiguration) {
        val random = Random()
        val margin = 150

        val enemyXPosition = random.nextInt(Gdx.graphics.width - margin) + width
        val enemyYPosition = Gdx.graphics.height.toFloat() + margin

        val enemyEntity: SpriteEntityWithLogic = createEntity(laserConfiguration, false) {
            x = enemyXPosition
            y = enemyYPosition
        } as SpriteEntityWithLogic

        (enemyEntity.logic as EnemyLogic).apply {
            shootDelay = 3f - Math.max(
                minimalShootDelay,
                totalCreatedEnemy / speedOfTheDecreasingEnemyShootDelayPerCreatedEnemy
            )
            speed = enemySpeed
        }
        enemyEntity.logic.apply { initialize.invoke(enemyEntity) }
        eventBus.sendEvent(RegisterEntity(enemyEntity))
    }

    override val onUpdate: EntityWithLogic.(Float) -> Unit = {
    }

    private fun printEnemiesStatistic() {
        Gdx.app.debug(
            this.javaClass.name,
            "Enemy destroyed. Total enemies: $totalCreatedEnemy Max enemies: $maxEnemiesOnStage, " +
                    "shoot delay: ${
                        minimalShootDelay.coerceAtLeast(totalCreatedEnemy / speedOfTheDecreasingEnemyShootDelayPerCreatedEnemy)
                    }"
        )
    }

}