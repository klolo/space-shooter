package pl.klolo.spaceshooter.game.physics

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import pl.klolo.spaceshooter.game.entity.Entity
import pl.klolo.spaceshooter.game.entity.kind.SpriteEntityWithLogic
import pl.klolo.spaceshooter.game.event.EventBus
import pl.klolo.spaceshooter.game.event.Collision

class ContactListener(private val eventBus: EventBus) : ContactListener {
    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
        if (contact?.fixtureB?.userData != null && contact.fixtureA?.userData != null) {
            val userDataA = contact.fixtureA.userData as Entity
            val userDataB = contact.fixtureB.userData as Entity

            if (userDataA is SpriteEntityWithLogic && userDataB is SpriteEntityWithLogic) {
                val collisionPoint = contact.worldManifold.points[0]
                eventBus.sendEvent(Collision(userDataA, collisionPoint.x, collisionPoint.y), userDataB.id)
                eventBus.sendEvent(Collision(userDataB, collisionPoint.x, collisionPoint.y), userDataA.id)
            }
        }
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {}

    override fun beginContact(contact: Contact?) {}

    override fun endContact(contact: Contact?) {}

}