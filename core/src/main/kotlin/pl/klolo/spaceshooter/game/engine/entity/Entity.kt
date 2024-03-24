package pl.klolo.spaceshooter.game.engine.entity

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import java.util.UUID

interface Entity {

    val uniqueName: String

    val layer: Int

    val id: String

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