package pl.klolo.spaceshooter.game.engine.inputProcessor

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.KeyEnterReleased
import pl.klolo.spaceshooter.game.event.EscapePressed
import pl.klolo.spaceshooter.game.event.KeyArrowLeftPressed
import pl.klolo.spaceshooter.game.event.KeyArrowLeftReleased
import pl.klolo.spaceshooter.game.event.KeyArrowRightPressed
import pl.klolo.spaceshooter.game.event.KeyArrowRightReleased
import pl.klolo.spaceshooter.game.event.KeySpaceReleased

/**
 * TODO: Przeniesc do desktop. Klasa zalezna od platformy gdzie jest uruchamiana gra.
 */
class KeyboardProcessor(private val eventBus: EventBus) : InputProcessor {
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> eventBus.sendEvent(KeyArrowLeftReleased)
            Input.Keys.RIGHT -> eventBus.sendEvent(KeyArrowRightReleased)
            Input.Keys.SPACE -> eventBus.sendEvent(KeySpaceReleased)
            Input.Keys.ENTER -> eventBus.sendEvent(KeyEnterReleased)
            Input.Keys.ESCAPE -> eventBus.sendEvent(EscapePressed)
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> eventBus.sendEvent(KeyArrowLeftPressed)
            Input.Keys.RIGHT -> eventBus.sendEvent(KeyArrowRightPressed)
        }
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

}