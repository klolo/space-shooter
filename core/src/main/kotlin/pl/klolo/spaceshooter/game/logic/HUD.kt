package pl.klolo.spaceshooter.game.logic

import com.badlogic.gdx.Gdx
import pl.klolo.spaceshooter.game.engine.entity.EntityRegistry
import pl.klolo.spaceshooter.game.engine.entity.kind.TextEntity
import pl.klolo.spaceshooter.game.engine.entity.createEntity
import pl.klolo.spaceshooter.game.engine.event.EventBus
import pl.klolo.spaceshooter.game.common.executeAfterDelay
import pl.klolo.spaceshooter.game.engine.entity.ActorEntity
import pl.klolo.spaceshooter.game.engine.entity.EntityConfiguration
import pl.klolo.spaceshooter.game.logic.player.bonusLifetime

class HUD(
    private val eventBus: EventBus,
    private val entityRegistry: EntityRegistry,
    entityConfiguration: EntityConfiguration
) : ActorEntity(entityConfiguration) {

    private val textConfiguration = entityRegistry.getConfigurationById("text")
    private val pointsLabel: TextEntity by lazy { initPointLabel() }
    private var points = 0
    private var doublePoints = false

    private fun initPointLabel(): TextEntity {
        return (createEntity(textConfiguration) as TextEntity)
            .apply {
                text = "0"
                eventBus.sendEvent(RegisterEntity(this))
            }
    }

    private fun initBonusLabel(): TextEntity {
        return (createEntity(textConfiguration) as TextEntity)
            .apply {
                text = ""
                eventBus.sendEvent(RegisterEntity(this))
            }
    }

    override fun onDispose() {
        pointsLabel.onDispose()
    }

    override fun onInitialize() {
        Gdx.app.debug(this.javaClass.name, "createSubscription")

        eventBus
            .subscribe(id)
            .onEvent<AddPoints> {
                addPoints(it)
                pointsLabel.text = "$points"
            }
            .onEvent<EnableDoublePoints> {
                doublePoints = true
                executeAfterDelay(bonusLifetime) {
                    doublePoints = false
                }
            }
    }

    override fun onUpdate(delta: Float) {
        val leftMargin = 20f
        pointsLabel.x = leftMargin
        pointsLabel.y = Gdx.graphics.height.toFloat() - pointsLabel.getFontHeight() * 1.2f
    }

    private fun addPoints(it: AddPoints) {
        points = when (doublePoints) {
            true -> points + (it.points * 2)
            false -> points + it.points
        }
    }
}