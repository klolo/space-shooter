package pl.klolo.spaceshooter.game.common

import com.badlogic.gdx.Gdx
import pl.klolo.spaceshooter.game.engine.Profile

fun getScreenWidth(profile: Profile): Float {
    return when (profile) {
        Profile.ANDROID -> Gdx.graphics.height.toFloat()
        else -> Gdx.graphics.width.toFloat()
    }
}

fun getScreenHeight(profile: Profile): Float {
    return when (profile) {
        Profile.ANDROID -> Gdx.graphics.width.toFloat()
        else -> Gdx.graphics.height.toFloat()
    }
}