package pl.klolo.spaceshooter.game.engine.entity

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import java.util.UUID

open class CustomEntity(entityConfiguration: EntityConfiguration) : Entity {

    override val uniqueName = entityConfiguration.uniqueName

    override val layer = entityConfiguration.layer

    override val id = UUID.randomUUID().toString()

    override var shouldBeRemove = false

    override var useLighting = false

    override fun onUpdate(delta: Float) {

    }

    override fun onDraw(batch: Batch, camera: OrthographicCamera) {

    }

    override fun onDispose() {

    }

    override fun onInitialize() {

    }

}