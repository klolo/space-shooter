package pl.klolo.spaceshooter.game.logic.bonus

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import pl.klolo.spaceshooter.game.engine.GameEngine
import pl.klolo.spaceshooter.game.engine.assetManager
import pl.klolo.spaceshooter.game.entity.EntityLogicWithRendering
import pl.klolo.spaceshooter.game.entity.EntityRegistry
import pl.klolo.spaceshooter.game.entity.kind.SpriteWithCustomRendering
import pl.klolo.spaceshooter.game.logic.player.bonusLifetime
import pl.klolo.spaceshooter.game.event.EnableDoublePoints
import pl.klolo.spaceshooter.game.event.EnableMagnetPoints
import pl.klolo.spaceshooter.game.event.EnableShield
import pl.klolo.spaceshooter.game.event.EnableSuperBullet
import pl.klolo.spaceshooter.game.event.EventBus

private data class BonusBarItem(
    val sprite: Sprite,
    var enabled: Boolean = false,
    var since: Long = 0
)

class PlayerBonusBarLogic(
    private val entityRegistry: EntityRegistry,
    private val eventBus: EventBus
) :
    EntityLogicWithRendering<SpriteWithCustomRendering> {

    private val entityScaleFactor =
        GameEngine.applicationConfiguration.getConfig("engine").getDouble("entityScaleFactor")
            .toFloat()
    private var posY =
        Gdx.graphics.height - entityRegistry.getConfigurationById("playerLifebar").height * 2
    private val initialPositionX =
        Gdx.graphics.width.toFloat() - entityRegistry.getConfigurationById("playerLifebar").width * 0.4f

    private val icons: List<BonusBarItem> = mutableListOf(
        BonusBarItem(
            Sprite(
                assetManager.get(
                    "power-ups/powerupBlue_star.png",
                    Texture::class.java
                )
            )
        ),
        BonusBarItem(
            Sprite(
                assetManager.get(
                    "power-ups/powerupBlue_bolt.png",
                    Texture::class.java
                )
            )
        ),
        BonusBarItem(
            Sprite(
                assetManager.get(
                    "power-ups/powerupBlue_shield.png",
                    Texture::class.java
                )
            )
        ),
        BonusBarItem(
            Sprite(
                assetManager.get(
                    "power-ups/powerupYellow_star.png",
                    Texture::class.java
                )
            )
        )
    )

    override val initialize: SpriteWithCustomRendering.() -> Unit = {
        icons.forEach {
            it.sprite.setSize(
                it.sprite.width * entityScaleFactor,
                it.sprite.height * entityScaleFactor
            )
        }
        useLighting = false

        eventBus
            .subscribe(id)
            .onEvent<EnableMagnetPoints> {
                icons[3].since = System.currentTimeMillis()
                icons[3].enabled = true
            }
            .onEvent<EnableSuperBullet> {
                icons[1].enabled = true
                icons[1].since = System.currentTimeMillis()
            }
            .onEvent<EnableShield> {
                icons[2].enabled = true
                icons[2].since = System.currentTimeMillis()
            }
            .onEvent<EnableDoublePoints> {
                icons[0].since = System.currentTimeMillis()
                icons[0].enabled = true
            }
    }

    override val draw: SpriteWithCustomRendering.(batch: Batch, camera: OrthographicCamera) -> Unit =
        { batch: Batch, _ ->
            val currentTime = System.currentTimeMillis()
            var posX = initialPositionX
            icons
                .filter { it.enabled }
                .forEach {
                    val opacity = ((currentTime - it.since).toFloat() / (bonusLifetime * 1000f))
                    if (opacity >= 1f) {
                        it.enabled = false
                    }
                    it.sprite.setPosition(posX, posY - it.sprite.height)
                    it.sprite.setColor(
                        it.sprite.color.r,
                        it.sprite.color.g,
                        it.sprite.color.b,
                        1f - opacity
                    );
                    it.sprite.draw(batch)
                    posX -= it.sprite.width + 10
                }
        }

    override val onUpdate: SpriteWithCustomRendering.(Float) -> Unit = {
    }

    override val onDispose: SpriteWithCustomRendering.() -> Unit = {

    }
}