package pl.klolo.game.logic

import box2dLight.Light
import com.badlogic.gdx.Gdx
import pl.klolo.game.common.Colors
import pl.klolo.game.engine.Profile
import pl.klolo.game.engine.*
import pl.klolo.game.entity.*
import pl.klolo.game.event.*
import pl.klolo.game.entity.EntityLogic
import pl.klolo.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.game.entity.kind.TextEntity
import pl.klolo.game.physics.GameLighting


class PulsingLightAnimation(private val light: Light) {
    var delta = 0f
    var deltaStep = 0.02f
    var minDistance = 150
    var distanceGrow = 350

    fun update() {
        delta += deltaStep
        light.distance = minDistance + (distanceGrow * Math.abs(Math.sin(delta.toDouble())).toFloat())
    }
}

class MainMenuLogic<T : Entity>(
        private val profileHolder: ProfileHolder,
        private val gameLighting: GameLighting,
        private val soundManager: SoundManager,
        private val eventProcessor: EventProcessor,
        private val entityRegistry: EntityRegistry) : EntityLogic<T> {

    private val textConfiguration = entityRegistry.getConfigurationById("text")

    private lateinit var pulsingLightAnimation: PulsingLightAnimation
    private lateinit var gameTitleLabel: TextEntity
    private lateinit var infoLabel: TextEntity

    override val initialize: T.() -> Unit = {
        Gdx.app.debug(this.javaClass.name, "createSubscription")

        eventProcessor
                .subscribe(id)
                .onEvent<PressedEnter> { eventProcessor.sendEvent(StartNewGame) }
                .onEvent<PressedEscape> { Gdx.app.exit() }

        soundManager.playSong(Song.MENU)
        infoLabel = createInfoLabel()
        gameTitleLabel = createGameTitleLabel()
        showGameLogo()
    }

    private fun showGameLogo() {
        val logoConfiguration = entityRegistry.getConfigurationById("gameLogo")
        val gameLogo: SpriteEntityWithLogic = createEntity(logoConfiguration) {
            width = logoConfiguration.width
            height = logoConfiguration.height
            x = Gdx.graphics.width.toFloat() / 2 - width / 2
            y = Gdx.graphics.height.toFloat() / 2 - height
        }

        val logoLight = gameLighting.createPointLight(100, Colors.ambient, 300f,
                Gdx.graphics.width.toFloat() / 2, gameLogo.y + gameLogo.height / 2)
        pulsingLightAnimation = PulsingLightAnimation(logoLight).apply { distanceGrow = 500 }

        eventProcessor.sendEvent(RegisterEntity(gameLogo))
    }

    override val onDispose: T.() -> Unit = {
        gameTitleLabel.dispose()
        infoLabel.dispose()
    }

    override val onUpdate: T.(Float) -> Unit = {
        pulsingLightAnimation.update()
    }

    private fun createGameTitleLabel(): TextEntity {
        return createEntity<TextEntity>(textConfiguration)
                .apply {
                    fontSize = FontSize.HUGE
                    text = "kotlin wars"
                    eventProcessor.sendEvent(RegisterEntity(this))
                }
                .apply {
                    intializeFont()
                }
                .apply {
                    x = Gdx.graphics.width.toFloat() / 2 - getFontWidth() / 2
                    y = Gdx.graphics.height.toFloat() / 2 + getFontHeight()
                }
    }

    private fun createInfoLabel(): TextEntity {
        return createEntity<TextEntity>(textConfiguration)
                .apply {
                    text = if (profileHolder.activeProfile == Profile.ANDROID) "touch to start" else "press enter for start, escape for exit"
                    fontSize = FontSize.SMALL
                    eventProcessor.sendEvent(RegisterEntity(this))
                }
                .apply {
                    intializeFont()
                }
                .apply {
                    x = Gdx.graphics.width.toFloat() / 2 - getFontWidth() / 2
                    y = getFontHeight() * 2
                }
    }
}