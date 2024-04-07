package pl.klolo.spaceshooter.game.logic.player.move

import com.badlogic.gdx.Gdx
import pl.klolo.spaceshooter.game.engine.GameEngine
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.KeyArrowDownPressed
import pl.klolo.spaceshooter.game.logic.KeyArrowDownReleased
import pl.klolo.spaceshooter.game.logic.KeyArrowLeftPressed
import pl.klolo.spaceshooter.game.logic.KeyArrowLeftReleased
import pl.klolo.spaceshooter.game.logic.KeyArrowRightPressed
import pl.klolo.spaceshooter.game.logic.KeyArrowRightReleased
import pl.klolo.spaceshooter.game.logic.KeyArrowUpPressed
import pl.klolo.spaceshooter.game.logic.KeyArrowUpReleased
import pl.klolo.spaceshooter.game.logic.player.Player

class DesktopPlayerMoveStrategy(
    private val player: Player,
    eventBus: EventBus
) : AbstractPlayerMoveStrategy(player, eventBus) {

    private val maxPlayerYPosition = Gdx.graphics.height * 0.3f

    private val playerSpeedYRation = 0.4f

    override fun initialize() {
        player.x = Gdx.graphics.width.toFloat() / 2 - player.width / 2
        playerSpeed = GameEngine.applicationConfiguration
            .getConfig("engine")
            .getDouble("playerSpeed")
            .toFloat()    // seconds per screen width
    }

    override fun subscribeEvents(eventBus: EventBus) =
        eventBus
            .subscribe(player.id)
            .onEvent<KeyArrowLeftPressed> {
                if (player.x - marginOffset >= 0) {
                    direction = Direction.LEFT
                    moveX(player.x - Gdx.graphics.width.toFloat(), playerSpeed)
                }
            }
            .onEvent<KeyArrowRightPressed> {
                if (player.x + player.width + marginOffset <= Gdx.graphics.width.toFloat()) {
                    direction = Direction.RIGHT
                    moveX(player.x + Gdx.graphics.width.toFloat(), playerSpeed)
                }
            }
            .onEvent<KeyArrowDownPressed> {
                if (player.y > playerBottomMargin()) {
                    direction = Direction.DOWN
                    moveY(playerBottomMargin(), playerSpeed * playerSpeedYRation)
                }
            }
            .onEvent<KeyArrowUpPressed> {
                if (player.y < maxPlayerYPosition) {
                    direction = Direction.DOWN
                    moveY(maxPlayerYPosition, playerSpeed * playerSpeedYRation)
                }
            }
            .onEvent<KeyArrowRightReleased> {
                if (direction == Direction.RIGHT) {
                    stopPlayer()
                }
            }
            .onEvent<KeyArrowLeftReleased> {
                if (direction == Direction.LEFT) {
                    stopPlayer()
                }
            }
            .onEvent<KeyArrowDownReleased> {
                if (direction == Direction.DOWN) {
                    stopPlayer()
                }
            }
            .onEvent<KeyArrowUpReleased> {
                if (direction == Direction.UP) {
                    stopPlayer()
                }
            }

    override fun playerBottomMargin() = player.height * 0.5f

    private fun stopPlayer() {
        direction = Direction.NONE
        player.removeAction(currentMove)
    }

}