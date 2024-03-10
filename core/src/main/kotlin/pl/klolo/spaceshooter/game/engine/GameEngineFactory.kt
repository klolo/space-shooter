package pl.klolo.spaceshooter.game.engine

import com.badlogic.gdx.assets.AssetManager
import pl.klolo.spaceshooter.game.entity.EntityRegistry
import pl.klolo.spaceshooter.game.event.EventProcessor
import pl.klolo.spaceshooter.game.physics.ContactListener
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.physics.GamePhysics

val gameDependencyInjectionContext = GameDependencyInjectionContext()
val assetManager = AssetManager()

var GAME_PROFILE = Profile.DESKTOP

class ProfileHolder(val activeProfile: Profile)

fun createGameEngine(profile: Profile): GameEngine {
    GAME_PROFILE = profile
    gameDependencyInjectionContext
            .apply {
                registerBean(EntityRegistry::class.java)
                registerBean(ProfileHolder(profile))
                registerBean(EventProcessor::class.java)
                registerBean(Highscore::class.java)
                registerBean(SoundManager::class.java)
                registerBean(ContactListener::class.java)
                registerBean(GamePhysics::class.java)
                registerBean(GameLighting::class.java)
                registerBean(Stage::class.java)
                registerBean(GameEngine::class.java)
            }

    return gameDependencyInjectionContext.getBeanByClass(GameEngine::class.java) as GameEngine
}