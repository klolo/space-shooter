package pl.klolo.spaceshooter.game.engine.inputProcessor

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.KeyEnterReleased
import pl.klolo.spaceshooter.game.logic.EscapePressed
import pl.klolo.spaceshooter.game.logic.KeyArrowDownPressed
import pl.klolo.spaceshooter.game.logic.KeyArrowDownReleased
import pl.klolo.spaceshooter.game.logic.KeyArrowLeftPressed
import pl.klolo.spaceshooter.game.logic.KeyArrowLeftReleased
import pl.klolo.spaceshooter.game.logic.KeyArrowRightPressed
import pl.klolo.spaceshooter.game.logic.KeyArrowRightReleased
import pl.klolo.spaceshooter.game.logic.KeyArrowUpPressed
import pl.klolo.spaceshooter.game.logic.KeyArrowUpReleased
import pl.klolo.spaceshooter.game.logic.KeySpaceReleased

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

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> eventBus.sendEvent(KeyArrowLeftPressed)
            Input.Keys.RIGHT -> eventBus.sendEvent(KeyArrowRightPressed)
            Input.Keys.DOWN -> eventBus.sendEvent(KeyArrowDownPressed)
            Input.Keys.UP -> eventBus.sendEvent(KeyArrowUpPressed)
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> eventBus.sendEvent(KeyArrowLeftReleased)
            Input.Keys.RIGHT -> eventBus.sendEvent(KeyArrowRightReleased)
            Input.Keys.UP -> eventBus.sendEvent(KeyArrowUpReleased)
            Input.Keys.DOWN -> eventBus.sendEvent(KeyArrowDownReleased)
            Input.Keys.SPACE -> eventBus.sendEvent(KeySpaceReleased)
            Input.Keys.ENTER -> eventBus.sendEvent(KeyEnterReleased)
            Input.Keys.ESCAPE -> eventBus.sendEvent(EscapePressed)
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

}