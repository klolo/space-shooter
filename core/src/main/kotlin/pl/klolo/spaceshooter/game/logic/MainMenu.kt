package pl.klolo.spaceshooter.game.logic

import box2dLight.Light
import com.badlogic.gdx.Gdx
import pl.klolo.spaceshooter.game.common.Colors
import pl.klolo.spaceshooter.game.engine.Profile
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.TextEntity
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.engine.FontSize
import pl.klolo.spaceshooter.game.engine.ProfileHolder
import pl.klolo.spaceshooter.game.engine.Song
import pl.klolo.spaceshooter.game.engine.SoundManager
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus


class PulsingLightAnimation(private val light: Light) {
    var delta = 0f
    var deltaStep = 0.02f
    var minDistance = 150
    var distanceGrow = 350

    fun update() {
        delta += deltaStep
        light.distance =
            minDistance + (distanceGrow * Math.abs(Math.sin(delta.toDouble())).toFloat())
    }
}

class MainMenu<T : ActorEntity>(
    private val profileHolder: ProfileHolder,
    private val gameLighting: GameLighting,
    private val soundManager: SoundManager,
    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry,
    entityConfiguration: EntityConfiguration
) : ActorEntity(entityConfiguration) {

    private val textConfiguration = entityRegistry.getConfigurationById("text")

    private lateinit var pulsingLightAnimation: PulsingLightAnimation
    private lateinit var gameTitleLabel: TextEntity
    private lateinit var infoLabel: TextEntity

    override fun onInitialize() {
        Gdx.app.debug(this.javaClass.name, "createSubscription")

        eventBus
            .subscribe(id)
            .onEvent<KeyEnterReleased> { eventBus.sendEvent(StartNewGame) }
            .onEvent<EscapePressed> { Gdx.app.exit() }

        soundManager.playSong(Song.MENU)
        infoLabel = createInfoLabel()
        gameTitleLabel = createGameTitleLabel()
        showGameLogo()
    }

    private fun showGameLogo() {
        val logoConfiguration = entityRegistry.getConfigurationById("gameLogo")
        val gameLogo: SpriteEntity = createEntity(logoConfiguration)
        gameLogo.apply {
            width = logoConfiguration.width
            height = logoConfiguration.height
            x = Gdx.graphics.width.toFloat() / 2 - width / 2
            y = Gdx.graphics.height.toFloat() / 2 - height
        }

        val logoLight = gameLighting.createPointLight(
            100, Colors.ambient, 300f,
            Gdx.graphics.width.toFloat() / 2, gameLogo.y + gameLogo.height / 2
        )
        pulsingLightAnimation = PulsingLightAnimation(logoLight).apply { distanceGrow = 500 }

        eventBus.sendEvent(RegisterEntity(gameLogo))
    }

    override fun onDispose() {
        gameTitleLabel.onDispose()
        infoLabel.onDispose()
    }

    override fun onUpdate(delta: Float) {
        pulsingLightAnimation.update()
    }

    private fun createGameTitleLabel(): TextEntity {
        return createEntity<TextEntity>(textConfiguration)
            .apply {
                fontSize = FontSize.HUGE
                text = "Galaxy wars"
                eventBus.sendEvent(RegisterEntity(this))
            }
            .apply {
                x = Gdx.graphics.width.toFloat() / 2 - getFontWidth() / 2
                y = Gdx.graphics.height.toFloat() / 2 + getFontHeight()
            }
    }

    private fun createInfoLabel(): TextEntity {
        return createEntity<TextEntity>(textConfiguration)
            .apply {
                text =
                    if (profileHolder.activeProfile == Profile.ANDROID) "touch to start" else "press enter for start, escape for exit"
                fontSize = FontSize.SMALL
                eventBus.sendEvent(RegisterEntity(this))
            }
            .apply {
                x = Gdx.graphics.width.toFloat() / 2 - getFontWidth() / 2
                y = getFontHeight() * 2
            }
    }
}