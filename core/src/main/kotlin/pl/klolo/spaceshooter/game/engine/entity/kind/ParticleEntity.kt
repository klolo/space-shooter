package pl.klolo.spaceshooter.game.engine.entity.kind

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import pl.klolo.spaceshooter.game.engine.GameEngine
import pl.klolo.spaceshooter.game.engine.assetManager
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration

open class ParticleEntity(
    entityConfiguration: EntityConfiguration
) : ActorEntity(entityConfiguration) {

    val effect = ParticleEffect()

    init {
        useLighting = true
    }

    private val entityScaleFactor = GameEngine.applicationConfiguration.getConfig("engine")
        .getDouble("entityScaleFactor")
        .toFloat()

    private val atlas = (assetManager.get("main.atlas", TextureAtlas::class.java))

    init {
        effect.load(Gdx.files.internal(entityConfiguration.file), atlas)
        effect.start()
        effect.scaleEffect(entityScaleFactor)
    }

    override fun onDraw(batch: Batch, camera: OrthographicCamera) {
        effect.draw(batch, Gdx.graphics.deltaTime)
    }

    override fun onUpdate(delta: Float) {
        effect.update(Gdx.graphics.deltaTime)
        effect.setPosition(x, y)
        super.act(Gdx.graphics.deltaTime)
    }

}