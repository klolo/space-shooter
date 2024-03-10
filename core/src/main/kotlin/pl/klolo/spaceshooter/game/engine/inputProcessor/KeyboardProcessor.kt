package pl.klolo.spaceshooter.game.engine.inputProcessor

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.PressedArrowDown
import pl.klolo.spaceshooter.game.event.PressedArrowUp
import pl.klolo.spaceshooter.game.event.PressedEnter
import pl.klolo.spaceshooter.game.event.PressedEscape
import pl.klolo.spaceshooter.game.event.PressedLeftDown
import pl.klolo.spaceshooter.game.event.PressedLeftUp
import pl.klolo.spaceshooter.game.event.PressedRightDown
import pl.klolo.spaceshooter.game.event.PressedRightUp
import pl.klolo.spaceshooter.game.event.PressedSpace

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
            Input.Keys.LEFT -> eventBus.sendEvent(PressedLeftUp)
            Input.Keys.RIGHT -> eventBus.sendEvent(PressedRightUp)
            Input.Keys.SPACE -> eventBus.sendEvent(PressedSpace)
            Input.Keys.DOWN -> eventBus.sendEvent(PressedArrowDown)
            Input.Keys.UP -> eventBus.sendEvent(PressedArrowUp)
            Input.Keys.ENTER -> eventBus.sendEvent(PressedEnter)
            Input.Keys.ESCAPE -> eventBus.sendEvent(PressedEscape)
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> eventBus.sendEvent(PressedLeftDown)
            Input.Keys.RIGHT -> eventBus.sendEvent(PressedRightDown)
        }
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

}