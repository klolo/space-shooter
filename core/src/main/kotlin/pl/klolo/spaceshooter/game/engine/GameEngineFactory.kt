package pl.klolo.spaceshooter.game.engine

import com.badlogic.gdx.assets.AssetManager
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.engine.physics.ContactListener
import pl.klolo.game.physics.GameLighting
import pl.klolo.spaceshooter.game.engine.physics.GamePhysics
import pl.klolo.spaceshooter.game.logic.Highscore

val dependencyInjectionContext = DependencyInjectionContext()
val assetManager = AssetManager()

var GAME_PROFILE = Profile.DESKTOP

class ProfileHolder(val activeProfile: Profile)

fun createGameEngine(profile: Profile): GameEngine {
    GAME_PROFILE = profile
    dependencyInjectionContext
            .apply {
                registerBean(EntityRegistry::class.java)
                registerBean(ProfileHolder(profile))
                registerBean(EventBus::class.java)
                registerBean(Highscore::class.java)
                registerBean(SoundManager::class.java)
                registerBean(ContactListener::class.java)
                registerBean(GamePhysics::class.java)
                registerBean(GameLighting::class.java)
                registerBean(Stage::class.java)
                registerBean(GameEngine::class.java)
            }

    return dependencyInjectionContext.getBeanByClass(GameEngine::class.java) as GameEngine
}