package pl.klolo.spaceshooter.game.logic.player.move

import pl.klolo.spaceshooter.game.engine.Profile
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.player.Player

enum class Direction { LEFT, RIGHT, NONE }

fun createMoveStrategy(profile: Profile, player: Player, eventBus: EventBus) =
    when (profile) {
        Profile.ANDROID -> AndroidPlayerMoveStrategy(player, eventBus)
        Profile.DESKTOP -> DesktopPlayerMoveStrategy(player, eventBus)
        else -> throw IllegalArgumentException("Profile not supported")
    }
