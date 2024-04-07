package pl.klolo.spaceshooter.game.logic

import pl.klolo.spaceshooter.game.engine.SoundEffect
import pl.klolo.spaceshooter.game.engine.entity.Entity

sealed class Event()

// keyboard
object KeyArrowLeftReleased : Event()

object KeyArrowRightReleased : Event()
object KeyArrowLeftPressed : Event()
object KeyArrowRightPressed : Event()
object KeySpaceReleased : Event()
object KeyEnterReleased : Event()
object EscapePressed : Event()

// player
class EnemyDestroyed(val x: Float = 0f, val y: Float = 0f) : Event()
class EnemyOutOfScreen : Event()
class BossCreated: Event()
class BossDestroyed: Event()

class PlayerChangePosition(val x: Float = 0f, val y: Float = 0f) : Event()
class ChangePlayerLifeLevel(val actualPlayerLifeLevel: Int = 100) : Event()
class AddPoints(val points: Int = 0) : Event()
class AddPlayerLife(val lifeAmount: Int = 0) : Event()

object EnableSuperBullet : Event()
object DisableSuperBullet : Event()

object EnableShield : Event()
object DisableShield : Event()

object EnableDoublePoints : Event()

object EnableMagnetPoints : Event()

object DisableMagnetPoints : Event()

object DisableDoublePoints : Event()

class LaserHitInShield(val x: Float = 0f, val y: Float = 0f) : Event()


// engine
class RegisterEntity(val entity: Entity? = null) : Event()

object GameOver : Event()
object StartNewGame : Event()
object OpenMainMenu : Event()

class Collision(
    val entity: Entity? = null,  // collided object
    val x: Float = 0f,  // collision position
    val y: Float = 0f
) : Event() // collision position

class PlaySound(val soundEffect: SoundEffect? = null) : Event()
object StopMusic : Event()