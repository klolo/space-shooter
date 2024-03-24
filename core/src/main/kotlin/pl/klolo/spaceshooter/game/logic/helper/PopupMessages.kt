package pl.klolo.spaceshooter.game.logic.helper

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import pl.klolo.spaceshooter.game.engine.entity.kind.TextEntity
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.RegisterEntity
import pl.klolo.spaceshooter.game.common.addSequence
import pl.klolo.spaceshooter.game.common.execute
import pl.klolo.spaceshooter.game.common.Colors

data class PopupMessageConfiguration(
    val message: String,
    val color: Color = Colors.white,
    val callback: () -> Unit = {}
)

class PopupMessages(
    private val entityRegistry: EntityRegistry,
    private val eventBus: EventBus
) {

    private var messageLabel: TextEntity? = null
    private val textEntityConfiguration = entityRegistry.getConfigurationById("text")

    var show: SpriteEntity.(popupMessageConfiguration: PopupMessageConfiguration) -> Unit = { popupMessageConfiguration ->
        if (messageLabel != null) {
            (messageLabel as TextEntity).shouldBeRemove = true
        }

        messageLabel = createEntity<TextEntity>(textEntityConfiguration)
                .apply {
                    text = popupMessageConfiguration.message
                    eventBus.sendEvent(RegisterEntity(this))
                    labelColor = popupMessageConfiguration.color
                }
                .apply {
                    addSequence(
                            alpha(0f, 0.01f),
                            alpha(1f, 0.1f),
                            alpha(0f, 1f),
                            execute {
                                messageLabel?.shouldBeRemove = true
                                messageLabel = null
                                popupMessageConfiguration.callback()
                            })
                }

        messageLabel!!.x = x + messageLabel!!.width / 2
        messageLabel!!.y = y + height
    }

    fun updatePosition(posX: Float, posY: Float) {
        messageLabel?.x = posX - (messageLabel!!.getFontWidth() / 2)
        messageLabel?.y = posY
    }
}