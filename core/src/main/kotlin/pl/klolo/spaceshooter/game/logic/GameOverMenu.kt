package pl.klolo.spaceshooter.game.logic

import com.badlogic.gdx.Gdx
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.engine.FontSize
import pl.klolo.spaceshooter.game.engine.Profile
import pl.klolo.spaceshooter.game.engine.ProfileHolder
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.TextEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus

class GameOverMenu<T : ActorEntity>(
    private val profileHolder: ProfileHolder,
    private val highscore: Highscore,
    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry,
    entityConfiguration: EntityConfiguration
) : ActorEntity(entityConfiguration) {

    private val textConfiguration = entityRegistry.getConfigurationById("text")

    private lateinit var infoLabel: TextEntity

    private lateinit var gameOverLabel: TextEntity

    private lateinit var scoreLabels: TextEntity

    private val activateNavigationTime = 1f

    override fun onInitialize() {
        Gdx.app.debug(this.javaClass.name, "createSubscription")

        executeAfterDelay(activateNavigationTime) {
            eventBus
                .subscribe(id)
                .onEvent<KeyEnterReleased> { eventBus.sendEvent(StartNewGame) }
                .onEvent<EscapePressed> { Gdx.app.exit() }
        }

        infoLabel = createInfoLabel()
        gameOverLabel = createGameOverLabel()
        scoreLabels = createScoreLabels()
    }

    private fun createGameOverLabel(): TextEntity {
        return (createEntity(textConfiguration) as TextEntity)
            .apply {
                text = "Game over"
                fontSize = FontSize.HUGE
                eventBus.sendEvent(RegisterEntity(this))
            }
            .apply {
                x = Gdx.graphics.width.toFloat() / 2 - getFontWidth() / 2
                y = Gdx.graphics.height.toFloat() / 2
            }
    }

    private fun createScoreLabels(): TextEntity {
        val startYPosition = Gdx.graphics.height.toFloat() / 2 - gameOverLabel.getFontHeight()

        return (createEntity(textConfiguration) as TextEntity)
            .apply {
                text =
                    "Your score: ${highscore.getLastScore()}\n\nBest score: ${highscore.getRecord()}"
                fontSize = FontSize.SMALL
                eventBus.sendEvent(RegisterEntity(this))
            }
            .apply {
                x = Gdx.graphics.width.toFloat() / 2 - getFontWidth() / 2
                y = startYPosition - getFontHeight() * 2
            }
    }

    private fun createInfoLabel(): TextEntity {
        return (createEntity(textConfiguration) as TextEntity)
            .apply {
                text =
                    if (profileHolder.activeProfile == Profile.ANDROID) "touch to start" else "press enter for start, escape for exit"
                fontSize = FontSize.SMALL
                eventBus.sendEvent(RegisterEntity(this))
            }
            .apply {
                x = Gdx.graphics.width.toFloat() / 2 - getFontWidth() / 2
                y = getFontHeight()
            }
    }

}