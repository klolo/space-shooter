package pl.klolo.spaceshooter.game.logic

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.engine.Profile
import pl.klolo.spaceshooter.game.engine.FontSize
import pl.klolo.spaceshooter.game.engine.Highscore
import pl.klolo.spaceshooter.game.engine.ProfileHolder
import pl.klolo.spaceshooter.game.entity.kind.TextEntity
import pl.klolo.spaceshooter.game.entity.Entity
import pl.klolo.spaceshooter.game.entity.EntityLogic
import pl.klolo.spaceshooter.game.entity.EntityRegistry
import pl.klolo.spaceshooter.game.entity.createEntity
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.KeyEnterReleased
import pl.klolo.spaceshooter.game.event.EscapePressed
import pl.klolo.spaceshooter.game.event.RegisterEntity
import pl.klolo.spaceshooter.game.event.StartNewGame

class GameOverMenuLogic<T : Entity>(
    private val profileHolder: ProfileHolder,
    private val highscore: Highscore,
    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry
) : EntityLogic<T>, Actor() {

    private val textConfiguration = entityRegistry.getConfigurationById("text")
    private lateinit var infoLabel: TextEntity
    private lateinit var gameOverLabel: TextEntity
    private lateinit var scoreLabels: TextEntity
    private val activateNavigationTime = 1f

    override val initialize: T.() -> Unit = {
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

    override val onDispose: T.() -> Unit = {
    }

    override val onUpdate: T.(Float) -> Unit = {
        super.act(it)
    }

    private fun createGameOverLabel(): TextEntity {
        return createEntity<TextEntity>(textConfiguration)
                .apply {
                    text = "Game over"
                    fontSize = FontSize.HUGE
                    eventBus.sendEvent(RegisterEntity(this))
                    intializeFont()
                }
                .apply {
                    x = Gdx.graphics.width.toFloat() / 2 - getFontWidth() / 2
                    y = Gdx.graphics.height.toFloat() / 2
                }
    }

    private fun createScoreLabels(): TextEntity {
        val startYPosition = Gdx.graphics.height.toFloat() / 2 - gameOverLabel.getFontHeight()

        return createEntity<TextEntity>(textConfiguration)
                .apply {
                    text = "Your score: ${highscore.getLastScore()}\n\nBest score: ${highscore.getRecord()}"
                    fontSize = FontSize.SMALL
                    eventBus.sendEvent(RegisterEntity(this))
                    intializeFont()
                }
                .apply {
                    x = Gdx.graphics.width.toFloat() / 2 - getFontWidth() / 2
                    y = startYPosition - getFontHeight() * 2
                }
    }

    private fun createInfoLabel(): TextEntity {
        return createEntity<TextEntity>(textConfiguration)
                .apply {
                    text = if (profileHolder.activeProfile == Profile.ANDROID) "touch to start" else "press enter for start, escape for exit"
                    fontSize = FontSize.SMALL
                    eventBus.sendEvent(RegisterEntity(this))
                }
                .apply {
                    intializeFont()
                }
                .apply {
                    x = Gdx.graphics.width.toFloat() / 2 - getFontWidth() / 2
                    y = getFontHeight()
                }
    }

}