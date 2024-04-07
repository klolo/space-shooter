package pl.klolo.spaceshooter.game.logic.obstacles

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import pl.klolo.spaceshooter.game.engine.GameEngine
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.RegisterEntity
import java.util.Random
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as execute
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence as runSequence

@Suppress("unused")
class ObstaclesGenerator(
    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry,
    entityConfiguration: EntityConfiguration
) : ActorEntity(entityConfiguration) {

    private var enemySpeed = 1f

    private var totalCreatedEnemy = 0

    private val random = Random()

    private val minimalDelayBetweenGeneration = 5

    private val enemies = listOf(
        "rock1",
        "rock2"
    )

    private var nextEnemy = enemies[random.nextInt(enemies.size)]

    override fun onInitialize() {
        enemySpeed = GameEngine.applicationConfiguration.getConfig("engine")
            .getDouble("enemySpeed")
            .toFloat()

        Gdx.app.debug(this.javaClass.name, "createSubscription")

        val generationAction = generationSequence()
        addAction(generationAction)
    }

    private fun generationSequence() = forever(
        runSequence(
            execute { generateEnemy() },
            delay(minimalDelayBetweenGeneration + 5f / random.nextInt(10)) // FIXME: Random.nextFloat doesnt work
        )
    )

    private fun generateEnemy() {
        val laserConfiguration = entityRegistry.getConfigurationById(nextEnemy)
        createEnemy(laserConfiguration)
        totalCreatedEnemy++
        nextEnemy = enemies[random.nextInt(enemies.size)]
    }

    private fun createEnemy(laserConfiguration: EntityConfiguration) {
        val random = Random()
        val margin = 150

        val enemyXPosition = random.nextInt(Gdx.graphics.width - margin) + width
        val enemyYPosition = Gdx.graphics.height.toFloat() + margin

        val obstacleEntity: SpriteEntity = createEntity<SpriteEntity>(laserConfiguration).apply {
            x = enemyXPosition
            y = enemyYPosition
        }

        val sizeOffset = random.nextInt(0, 30)
        val abstractObstacle = (obstacleEntity as AbstractObstacle)
        abstractObstacle.speed = enemySpeed
        abstractObstacle.width += sizeOffset
        abstractObstacle.height += sizeOffset
        abstractObstacle.onInitialize()

        eventBus.sendEvent(RegisterEntity(obstacleEntity))
    }

}