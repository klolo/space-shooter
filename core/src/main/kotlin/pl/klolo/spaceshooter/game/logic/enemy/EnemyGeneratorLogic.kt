package pl.klolo.spaceshooter.game.logic.enemy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import pl.klolo.spaceshooter.game.engine.GameEngine
import pl.klolo.spaceshooter.game.engine.ProfileHolder
import pl.klolo.spaceshooter.game.event.EnemyDestroyed
import pl.klolo.spaceshooter.game.event.EventProcessor
import pl.klolo.spaceshooter.game.event.RegisterEntity
import pl.klolo.spaceshooter.game.entity.EntityLogic
import pl.klolo.spaceshooter.game.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.entity.EntityRegistry
import pl.klolo.spaceshooter.game.entity.createEntity
import pl.klolo.spaceshooter.game.entity.kind.EntityWithLogic
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import java.util.*

const val speedOfTheDecreasingEnemyShootDelayPerCreatedEnemy = 500f
const val minimalShootDelay = 0.5f

class EnemyGeneratorLogic(
    private val profileHolder: ProfileHolder,
    private val eventProcessor: EventProcessor,
    private val entityRegistry: EntityRegistry
) : EntityLogic<EntityWithLogic> {

    private var enemySpeed = 1f
    private var maxEnemiesOnStage = 3
    private var enemiesCount = 0
    private var totalCreatedEnemy = 0

    override val onDispose: EntityWithLogic.() -> Unit = {

    }

    override val initialize: EntityWithLogic.() -> Unit = {
        enemySpeed = GameEngine.applicationConfiguration.getConfig("engine")
                .getDouble("enemySpeed")
                .toFloat()

        Gdx.app.debug(this.javaClass.name, "createSubscription")
        eventProcessor
                .subscribe(id)
                .onEvent<EnemyDestroyed> {
                    Gdx.app.debug(this.javaClass.name, "Enemy destroyed. Total enemies: $totalCreatedEnemy Max enemies: $maxEnemiesOnStage, " +
                            "shoot delay: ${Math.max(minimalShootDelay, totalCreatedEnemy / speedOfTheDecreasingEnemyShootDelayPerCreatedEnemy)}")
                    enemiesCount--
                }

        addAction(Actions.forever(
                Actions.sequence(
                        Actions.run {
                            if (enemiesCount < maxEnemiesOnStage) {
                                val laserConfiguration = entityRegistry.getConfigurationById("enemyRed" + (1 + Random().nextInt(5)))
                                enemiesCount++
                                totalCreatedEnemy++
                                createEnemy(laserConfiguration)

                                maxEnemiesOnStage = Math.max(Math.floorDiv(totalCreatedEnemy, 10), 3)
                            }
                        },
                        Actions.delay(1f)
                )
        ))
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
            shootDelay = 3f - Math.max(minimalShootDelay, totalCreatedEnemy / speedOfTheDecreasingEnemyShootDelayPerCreatedEnemy)
            speed = enemySpeed
        }
        enemyEntity.logic.apply { initialize.invoke(enemyEntity) }
        eventProcessor.sendEvent(RegisterEntity(enemyEntity))
    }

    override val onUpdate: EntityWithLogic.(Float) -> Unit = {
    }

}