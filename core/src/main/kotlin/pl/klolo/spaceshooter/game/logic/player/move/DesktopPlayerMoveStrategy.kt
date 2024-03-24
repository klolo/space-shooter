package pl.klolo.spaceshooter.game.logic.player.move

import com.badlogic.gdx.Gdx
import pl.klolo.spaceshooter.game.engine.GameEngine
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.KeyArrowLeftPressed
import pl.klolo.spaceshooter.game.logic.KeyArrowLeftReleased
import pl.klolo.spaceshooter.game.logic.KeyArrowRightPressed
import pl.klolo.spaceshooter.game.logic.KeyArrowRightReleased
import pl.klolo.spaceshooter.game.logic.player.Player

class DesktopPlayerMoveStrategy(
    private val player: Player
) : AbstractPlayerMoveStrategy(player) {

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
                if (player.x - player.width > 0) {
                    direction = Direction.LEFT
                    move(player.x - Gdx.graphics.width.toFloat(), playerSpeed)
                }
            }
            .onEvent<KeyArrowRightPressed> {
                if (player.x + player.width < Gdx.graphics.width.toFloat()) {
                    direction = Direction.RIGHT
                    move(player.x + Gdx.graphics.width.toFloat(), playerSpeed)
                }
            }
            .onEvent<KeyArrowRightReleased> {
                if (direction == Direction.RIGHT) {
                    direction = Direction.NONE
                    player.removeAction(currentMove)
                }
            }
            .onEvent<KeyArrowLeftReleased> {
                if (direction == Direction.LEFT) {
                    direction = Direction.NONE
                    player.removeAction(currentMove)
                }
            }

}