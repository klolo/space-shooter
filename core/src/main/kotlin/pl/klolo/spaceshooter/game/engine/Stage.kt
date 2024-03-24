package pl.klolo.spaceshooter.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.beust.klaxon.Klaxon
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.Entity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.GameOver
import pl.klolo.spaceshooter.game.logic.OpenMainMenu
import pl.klolo.spaceshooter.game.logic.RegisterEntity
import pl.klolo.spaceshooter.game.logic.StartNewGame

class Stage(
    private val gameLighting: GameLighting,
    private val soundManager: SoundManager,
    private val gamePhysics: GamePhysics,
    private val entityRegistry: EntityRegistry,
    private val eventBus: EventBus
) {

    private var entities = emptyList<Entity>()

    fun initEntities() {
        Gdx.app.debug(this.javaClass.name, "createSubscription")

        subscribe()
        loadStage("entities/loader-entities.json")
    }

    private fun subscribe() {
        eventBus
            .subscribe("Stage")
            .onEvent<OpenMainMenu> {
                switchStage("entities/menu-entities.json")
            }
            .onEvent<RegisterEntity> {
                val newEntity = it.entity
                if (newEntity != null) {
                    entities += newEntity
                    Gdx.app.debug(
                        this.javaClass.name,
                        "register entity uniqueName = ${newEntity.uniqueName}. Total bodies: ${gamePhysics.world.bodyCount}"
                    )
                }
            }
            .onEvent<GameOver> {
                Gdx.app.debug(this.javaClass.name, "game over")
                switchStage("entities/gameover-menu-entities.json")
                soundManager.playSong(Song.MENU)
            }
            .onEvent<StartNewGame> {
                Gdx.app.debug(this.javaClass.name, "start new game")
                switchStage("entities/game-entities.json")
                soundManager.playSong(Song.GAME)
            }
    }

    private fun switchStage(nextStageFileConfiguration: String) {
        disposeCurrentStageEntities()
        subscribe()
        loadStage(nextStageFileConfiguration)
        soundManager.initialize()
    }

    private fun disposeCurrentStageEntities() {
        eventBus.clearAllSubscription()
        gamePhysics.onDispose()

        Gdx.app.debug(this.javaClass.name, "clearing entities")
        entities.forEach { it.onDispose() }
        entities = emptyList()

        gameLighting.clearLights()
    }

    private fun loadStage(stageConfigurationFilename: String) {
        val entityScaleFactor = GameEngine.applicationConfiguration.getConfig("engine")
            .getDouble("entityScaleFactor")
            .toFloat()

        val json = Gdx.files.internal(stageConfigurationFilename).readString()

        val entitiesConfiguration = Klaxon()
            .parseArray<EntityConfiguration>(json)
            ?.map { scaleEntity(it, entityScaleFactor) } ?: emptyList()

        entityRegistry.addConfiguration(entitiesConfiguration)
        val loadedEntities = entitiesConfiguration
            .filter { it.initOnCreate }
            .map { createEntity<ActorEntity>(it) }

        entities += loadedEntities
        entities = entities.sortedBy { it.layer }
        Gdx.app.debug(
            this.javaClass.name,
            "entities loaded: ${entities.joinToString { it.uniqueName }}"
        )
    }

    private fun scaleEntity(
        entityConfiguration: EntityConfiguration,
        entityScaleFactor: Float
    ): EntityConfiguration {
        return EntityConfiguration(
            uniqueName = entityConfiguration.uniqueName,
            javaClass = entityConfiguration.javaClass,
            file = entityConfiguration.file,
            x = entityConfiguration.x,
            y = entityConfiguration.y,
            width = entityConfiguration.width * entityScaleFactor,
            height = entityConfiguration.height * entityScaleFactor,
            layer = entityConfiguration.layer,
            initOnCreate = entityConfiguration.initOnCreate,
            useLighting = entityConfiguration.useLighting
        )
    }

    fun update(delta: Float) {
        entities
            .filter { it.shouldBeRemove }
            .forEach {
                Gdx.app.debug(this.javaClass.name, "dispose entity: ${it.uniqueName}")
                it.onDispose()
            }

        entities = entities.filter { !it.shouldBeRemove }
        entities.forEach { it.onUpdate(delta) }
    }

    fun dispose() {
        entities.forEach(Entity::onDispose)
    }

    fun drawWithLight(batch: Batch, camera: OrthographicCamera) {
        entities
            .filter { it.useLighting }
            .forEach { it.onDraw(batch, camera) }
    }

    fun drawWithoutLight(batch: Batch, camera: OrthographicCamera) {
        entities
            .filter { !it.useLighting }
            .forEach { it.onDraw(batch, camera) }
    }

}