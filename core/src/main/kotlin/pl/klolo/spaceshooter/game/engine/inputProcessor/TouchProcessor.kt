package pl.klolo.spaceshooter.game.engine.inputProcessor

import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.PressedEnter
import pl.klolo.spaceshooter.game.event.PressedLeftUp
import pl.klolo.spaceshooter.game.event.PressedRightUp
import pl.klolo.spaceshooter.game.event.PressedSpace


class TouchProcessor(eventBus: EventBus) : GestureDetector(GestureListener(eventBus))

class GestureListener(private val eventBus: EventBus) : GestureDetector.GestureListener {
    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        eventBus.sendEvent(PressedEnter)
        return true
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        eventBus.sendEvent(PressedSpace)
        eventBus.sendEvent(PressedRightUp)
        eventBus.sendEvent(PressedLeftUp)
        return true
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean = true

    override fun zoom(initialDistance: Float, distance: Float): Boolean = true

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float) = true

    override fun pinchStop() {
    }

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean = true

    override fun longPress(x: Float, y: Float) = true

    override fun pinch(initialPointer1: Vector2?, initialPointer2: Vector2?, pointer1: Vector2?, pointer2: Vector2?): Boolean = true

}