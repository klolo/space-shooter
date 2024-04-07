package pl.klolo.spaceshooter.game.logic.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import pl.klolo.spaceshooter.game.engine.assetManager
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.logic.ChangePlayerLifeLevel
import pl.klolo.spaceshooter.game.engine.event.EventBus
import kotlin.math.max

class PlayerLifeBar(
    private val eventBus: EventBus,
    private val entityConfiguration: EntityConfiguration
) : ActorEntity(entityConfiguration) {

    private lateinit var fill: Sprite

    private lateinit var background: Sprite

    private var lifeAmount = 1f

    override fun onInitialize() {
        Gdx.app.debug(this.javaClass.name, "createSubscription")

        fill = Sprite(assetManager.get(entityConfiguration.file, Texture::class.java))
        background = Sprite(assetManager.get("lifebar.png", Texture::class.java))

        useLighting = false
        width = entityConfiguration.width
        height = entityConfiguration.height

        eventBus
            .subscribe(id)
            .onEvent<ChangePlayerLifeLevel> {
                lifeAmount = it.actualPlayerLifeLevel / 100f
            }
    }

    override fun onDraw(batch: Batch, camera: OrthographicCamera) {
        val batchColor = batch.color
        batch.color = getLifebarColor()
        batch.draw(
            background,
            x,
            y,
            originX,
            originY,
            entityConfiguration.width,
            height,
            scaleX,
            scaleY,
            rotation
        )
        batch.draw(
            fill,
            x + (entityConfiguration.width * 0.05f),
            y + (entityConfiguration.height * 0.2f),
            originX, originY, width, height * 0.6f, scaleX, scaleY, rotation
        )
        batch.color = batchColor
    }

    private fun getLifebarColor(): Color {
        // since 80% of player life we are start reducing blue and green color but not more then 50%
        val colorAmount = max(lifeAmount + 0.2f, 0.5f)
        return Color(1.0f, colorAmount, colorAmount, 1f)
    }

    override fun onUpdate(delta: Float) {
        x = Gdx.graphics.width.toFloat() - entityConfiguration.width - 20.0f
        width = Math.max(0f, (entityConfiguration.width * 0.9f) * lifeAmount)
        y = Gdx.graphics.height.toFloat() - height - 20.0f
    }
}