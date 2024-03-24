package pl.klolo.spaceshooter.game.engine.entity.kind

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration

open class SpriteEntity(
    entityConfiguration: EntityConfiguration,
    var sprite: Sprite,
) : ActorEntity(entityConfiguration) {

    var display = true

    init {
        useLighting = true
        x = entityConfiguration.x
        y = entityConfiguration.y
        width = entityConfiguration.width
        height = entityConfiguration.height
        sprite.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    override fun positionChanged() {
        sprite.setPosition(x, y)
        super.positionChanged()
    }

    override fun onDraw(batch: Batch, camera: OrthographicCamera) {
        if (display) {
            batch.draw(sprite, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        }
    }

}