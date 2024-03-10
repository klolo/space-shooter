package pl.klolo.spaceshooter.game.logic.player

import pl.klolo.spaceshooter.game.engine.Profile

fun getPlayerBottomMargin(profile: Profile, playerHeight: Float): Float {
    return when (profile) {
        Profile.ANDROID -> playerHeight * 0.8f
        else -> playerHeight
    }
}