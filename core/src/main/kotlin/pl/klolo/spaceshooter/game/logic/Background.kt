package pl.klolo.spaceshooter.game.logic

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import pl.klolo.spaceshooter.game.engine.Profile
import pl.klolo.spaceshooter.game.engine.ProfileHolder
import pl.klolo.spaceshooter.game.engine.assetManager
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity

class Background(
    private val profileHolder: ProfileHolder,
    private val entityConfiguration: EntityConfiguration,
    sprite: Sprite
) : SpriteEntity(entityConfiguration, sprite) {

    companion object {

        @JvmStatic
        private lateinit var firstBackground: Sprite

        @JvmStatic
        private lateinit var secondBackground: Sprite

        @JvmStatic
        private lateinit var thirdBackground: Sprite

        private var positionInitialized = false
    }

    private val movingSpeed = 20
    private val isPortrait = profileHolder.activeProfile == Profile.ANDROID

    override fun onInitialize() {
        Gdx.app.debug(this.javaClass.name, "createSubscription")

        val imageToLoad = if (isPortrait) "background-portrait.jpg" else entityConfiguration.file

        if (!positionInitialized) {
            val texture = assetManager.get(imageToLoad, Texture::class.java)
            firstBackground = Sprite(texture)

            if (isPortrait) {
                firstBackground.y = Gdx.graphics.height.toFloat()
            }
            else {
                firstBackground.x = Gdx.graphics.width.toFloat() * -1
            }

            secondBackground = Sprite(texture)
            thirdBackground = Sprite(texture)

            if (isPortrait) {
                thirdBackground.y = Gdx.graphics.height.toFloat() * -1
            }
            else {
                thirdBackground.x = Gdx.graphics.width.toFloat()
            }

            positionInitialized = true
        }
    }

    override fun onDraw(batch: Batch, camera: OrthographicCamera) {
        batch.disableBlending() // for performance purpose when drawing big texture
        batch.draw(
            firstBackground,
            firstBackground.x,
            firstBackground.y,
            originX,
            originY,
            width,
            height,
            scaleX,
            scaleY,
            rotation
        )
        batch.draw(
            secondBackground,
            secondBackground.x,
            secondBackground.y,
            originX,
            originY,
            width,
            height,
            scaleX,
            scaleY,
            rotation
        )
        batch.draw(
            thirdBackground,
            thirdBackground.x,
            thirdBackground.y,
            originX,
            originY,
            width,
            height,
            scaleX,
            scaleY,
            rotation
        )
        batch.enableBlending()
    }

    override fun onUpdate(delta: Float) {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()

        if (isPortrait) {
            firstBackground.y += delta * movingSpeed
            secondBackground.y += delta * movingSpeed
            thirdBackground.y += delta * movingSpeed

            if (firstBackground.y > Gdx.graphics.height.toFloat() * 1.5) {
                val temp = firstBackground
                firstBackground = secondBackground
                secondBackground = thirdBackground
                temp.y = thirdBackground.y - Gdx.graphics.height.toFloat()
                thirdBackground = temp
            }
        }
        else {
            firstBackground.x -= delta * movingSpeed
            secondBackground.x -= delta * movingSpeed
            thirdBackground.x -= delta * movingSpeed

            if (firstBackground.x < Gdx.graphics.width.toFloat() * -1.5) {
                val temp = firstBackground
                firstBackground = secondBackground
                secondBackground = thirdBackground
                temp.x = thirdBackground.x + Gdx.graphics.width.toFloat()
                thirdBackground = temp
            }
        }
    }
}