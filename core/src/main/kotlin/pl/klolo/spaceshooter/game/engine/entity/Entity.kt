package pl.klolo.spaceshooter.game.engine.entity

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch

interface Entity {

    val uniqueName: String

    val layer: Int

    val id: Int

    var shouldBeRemove: Boolean

    var useLighting: Boolean

    fun onUpdate(delta: Float) {
    }

    fun onDraw(batch: Batch, camera: OrthographicCamera) {

    }

    fun onDispose() {

    }

    fun onInitialize() {

    }

}