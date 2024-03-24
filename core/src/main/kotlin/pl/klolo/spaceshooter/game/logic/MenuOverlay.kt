package pl.klolo.spaceshooter.game.logic

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import pl.klolo.spaceshooter.game.engine.assetManager
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration

class MenuOverlay(
    entityConfiguration: EntityConfiguration
) : ActorEntity(entityConfiguration) {

    private lateinit var leftTop: Sprite

    private lateinit var rightBottom: Sprite

    private lateinit var rightTop: Sprite

    private lateinit var middleBottom: Sprite

    private lateinit var clouds: Sprite

    override fun onInitialize() {
        leftTop = Sprite(assetManager.get("left_top_menu.png", Texture::class.java))
        leftTop.setPosition(0f, Gdx.graphics.height.toFloat() - leftTop.texture.height)

        rightBottom = Sprite(assetManager.get("right_bottom_menu.png", Texture::class.java))
        rightBottom.setPosition(Gdx.graphics.width.toFloat() - rightBottom.texture.width, 0f)

        rightTop = Sprite(assetManager.get("right_top_menu.png", Texture::class.java))
        rightTop.setPosition(
            Gdx.graphics.width.toFloat() - rightTop.texture.width,
            Gdx.graphics.height.toFloat() - rightTop.texture.height
        )

        middleBottom = Sprite(assetManager.get("middle_bottom.png", Texture::class.java))
        middleBottom.setPosition(0f, 0f)

        clouds = Sprite(assetManager.get("menu_cloud.png", Texture::class.java))
        clouds.setPosition(0f, 0f)
        clouds.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        useLighting = false
    }

    override fun onDraw(batch: Batch, camera: OrthographicCamera) {
        clouds.draw(batch)
        leftTop.draw(batch)
        rightBottom.draw(batch)
        rightTop.draw(batch)
        middleBottom.draw(batch)
    }

}