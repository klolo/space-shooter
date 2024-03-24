package pl.klolo.spaceshooter.game.logic.player.shield

import com.badlogic.gdx.scenes.scene2d.Action
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.engine.SoundEffect
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.logic.DisableShield
import pl.klolo.spaceshooter.game.logic.EnableShield
import pl.klolo.spaceshooter.game.logic.PlaySound
import pl.klolo.spaceshooter.game.logic.player.Player
import pl.klolo.spaceshooter.game.logic.player.bonusLifetime

class PlayerShieldStrategy(
    private val eventBus: EventBus,
    private val player: Player
) {

    var hasShield = false

    var disableShieldAction: Action? = null

    fun subscribeEvents() =
        eventBus.subscribe(player.id)
            .onEvent<EnableShield> { onEnableShield() }

    private fun onEnableShield() {
        eventBus.sendEvent(PlaySound(SoundEffect.FOUND_BONUS))
        hasShield = true

        if (disableShieldAction != null) {
            player.removeAction(disableShieldAction)
        }

        player.apply {
            disableShieldAction = executeAfterDelay(bonusLifetime) {
                hasShield = false
                eventBus.sendEvent(DisableShield)
            }
        }

    }
}