package pl.klolo.spaceshooter.game.logic

import pl.klolo.spaceshooter.game.entity.Entity
import pl.klolo.spaceshooter.game.entity.EntityLogic

class EmptyLogic<T : Entity> : EntityLogic<T> {
    override val initialize: T.() -> Unit = {

    }

    override val onDispose: T.() -> Unit = {

    }

    override val onUpdate: T.(Float) -> Unit = {

    }
}