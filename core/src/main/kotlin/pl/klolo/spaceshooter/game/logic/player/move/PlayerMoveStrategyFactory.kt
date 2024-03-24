package pl.klolo.spaceshooter.game.logic.player.move

import pl.klolo.spaceshooter.game.engine.Profile
import pl.klolo.spaceshooter.game.logic.player.Player

enum class Direction { LEFT, RIGHT, NONE }

fun createMoveStrategy(profile: Profile, player: Player) =
    when (profile) {
        Profile.ANDROID -> AndroidPlayerMoveStrategy(player)
        Profile.DESKTOP -> DesktopPlayerMoveStrategy(player)
        else -> throw IllegalArgumentException("Profile not supported")
    }
