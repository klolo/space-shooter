package pl.klolo.spaceshooter.game.logic.enemy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence as runSequence
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as execute
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import pl.klolo.spaceshooter.game.engine.GameEngine
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.logic.EnemyDestroyed
import pl.klolo.spaceshooter.game.logic.EnemyOutOfScreen
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.AddPoints
import pl.klolo.spaceshooter.game.logic.BossCreated
import pl.klolo.spaceshooter.game.logic.BossDestroyed
import pl.klolo.spaceshooter.game.logic.RegisterEntity
import java.util.Random
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

const val speedOfTheDecreasingEnemyShootDelayPerCreatedEnemy = 500f
const val minimalShootDelay = 0.8f

@Suppress("unused")
class EnemyGenerator(
    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry,
    entityConfiguration: EntityConfiguration
) : ActorEntity(entityConfiguration) {

    private var enemySpeed = 1f
    private var maxEnemiesOnStage = 3
    private var enemiesCount = AtomicInteger(0)
    private var totalCreatedEnemy = 0
    private val random = Random()
    private var bossActive = false

    private var bossCount = 0
    private var pointsToNextBoss = 0

    private val enemies = listOf(
        "enemyRed1",
        "enemyRed2",
        "enemyRed3",
        "enemyRed4",
        "enemyRed5",
    )

    private var nextEnemy = enemies[random.nextInt(enemies.size)]

    override fun onInitialize() {
        enemySpeed = GameEngine.applicationConfiguration.getConfig("engine")
            .getDouble("enemySpeed")
            .toFloat()

        Gdx.app.debug(this.javaClass.name, "createSubscription")

        val generationAction = generationSequence()
        addAction(generationAction)

        eventBus
            .subscribe(id)
            .onEvent<EnemyDestroyed> {
                printEnemiesStatistic()
                enemiesCount.decrementAndGet()
            }
            .onEvent<BossDestroyed> {
                bossCount -= 1
                if (bossCount <= 0) {
                    addAction(generationAction)
                    bossActive = false
                }
            }
            .onEvent<EnemyOutOfScreen> {
                printEnemiesStatistic()
                enemiesCount.decrementAndGet()
            }
            .onEvent<AddPoints> {
                pointsToNextBoss += it.points

                if (pointsToNextBoss >= 1000 && !bossActive) {
                    nextEnemy = "boss1"
                    bossActive = true
                    pointsToNextBoss = 0
                }
            }
    }

    private fun generationSequence() = forever(
        forever(
            runSequence(
                execute {
                    if (enemiesCount.get() < maxEnemiesOnStage) {
                        generateEnemy()
                    }
                },
                delay(2f / random.nextInt(4)) // FIXME: Random.nextFloat doesnt work
            )
        )
    )

    private fun generateEnemy() {
        val laserConfiguration = entityRegistry.getConfigurationById(nextEnemy)
        createEnemy(laserConfiguration)

        enemiesCount.incrementAndGet()
        totalCreatedEnemy++
        maxEnemiesOnStage = max(Math.floorDiv(totalCreatedEnemy, 10), maxEnemiesOnStage)
        nextEnemy = enemies[random.nextInt(enemies.size)]
    }

    private fun createEnemy(laserConfiguration: EntityConfiguration) {
        val random = Random()
        val margin = 150

        val enemyXPosition = random.nextInt(Gdx.graphics.width - margin) + width
        val enemyYPosition = Gdx.graphics.height.toFloat() + margin

        val enemyEntity: SpriteEntity = createEntity<SpriteEntity>(laserConfiguration).apply {
            x = enemyXPosition
            y = enemyYPosition
        }

        val shootDelay = 3f - max(
            minimalShootDelay,
            totalCreatedEnemy / speedOfTheDecreasingEnemyShootDelayPerCreatedEnemy
        );

        val abstractEnemy = (enemyEntity as AbstractEnemy)
        abstractEnemy.shootDelay = shootDelay
        abstractEnemy.speed = enemySpeed
        abstractEnemy.onInitialize()

        eventBus.sendEvent(RegisterEntity(enemyEntity))
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