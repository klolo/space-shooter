package pl.klolo.spaceshooter.game.engine.inputProcessor

import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.KeyEnterReleased
import pl.klolo.spaceshooter.game.event.KeyArrowLeftReleased
import pl.klolo.spaceshooter.game.event.KeyArrowRightReleased
import pl.klolo.spaceshooter.game.event.KeySpaceReleased


class TouchProcessor(eventBus: EventBus) : GestureDetector(GestureListener(eventBus))

class GestureListener(private val eventBus: EventBus) : GestureDetector.GestureListener {
    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        eventBus.sendEvent(KeyEnterReleased)
        return true
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        eventBus.sendEvent(KeySpaceReleased)
        eventBus.sendEvent(KeyArrowRightReleased)
        eventBus.sendEvent(KeyArrowLeftReleased)
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