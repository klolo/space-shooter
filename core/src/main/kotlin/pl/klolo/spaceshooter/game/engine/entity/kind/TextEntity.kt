package pl.klolo.spaceshooter.game.engine.entity.kind

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.ui.Label
import pl.klolo.spaceshooter.game.common.Colors
import pl.klolo.spaceshooter.game.engine.FontManager
import pl.klolo.spaceshooter.game.engine.FontSize
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.CustomEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration

open class TextEntity(
    entityConfiguration: EntityConfiguration,
) : CustomEntity(entityConfiguration) {

    val label: Label

    var text: String = ""

    var fontSize = FontSize.SMALL

    var labelColor = Colors.white

    var x: Float

    var y: Float

    var width: Float

    var height: Float

    var alpha: Float

    init {
        x = entityConfiguration.x
        y = entityConfiguration.y
        width = entityConfiguration.width
        height = entityConfiguration.height

        label = FontManager.getFontBySize(fontSize)
        label.setSize(fontSize.value.toFloat(), fontSize.value.toFloat())
        label.setText(text)
        alpha = 0.9f
    }

    override fun onUpdate(delta: Float) {
        label.setPosition(x, y)
        label.setSize(0f, fontSize.value.toFloat())
        label.setText(text)
        label.color = labelColor
    }

    override fun onDraw(batch: Batch, camera: OrthographicCamera) {
        label.draw(batch, alpha)
    }

    fun getFontHeight() = FontManager.getFontBySize(fontSize).height

    fun getFontWidth() = GlyphLayout(label.style?.font, text).width

}