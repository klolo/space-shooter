package pl.klolo.spaceshooter.game.engine.entity

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import java.util.UUID

open class ActorEntity(entityConfiguration: EntityConfiguration) : Actor(), Entity {

    override val uniqueName = entityConfiguration.uniqueName

    override val layer = entityConfiguration.layer

    override val id = UUID.randomUUID().toString()

    override var shouldBeRemove = false

    override var useLighting = false

    override fun onUpdate(delta: Float) {
        super.act(delta)
    }

    override fun onDraw(batch: Batch, camera: OrthographicCamera) {

    }

    override fun onDispose() {

    }

    override fun onInitialize() {

    }

}