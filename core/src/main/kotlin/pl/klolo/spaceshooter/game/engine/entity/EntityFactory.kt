package pl.klolo.spaceshooter.game.engine.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import pl.klolo.spaceshooter.game.engine.assetManager
import pl.klolo.spaceshooter.game.engine.dependencyInjectionContext
import pl.klolo.spaceshooter.game.engine.entity.kind.SpriteEntity
import java.lang.Exception

var entityCounter = 0

@Suppress("UNCHECKED_CAST")
fun <T : Entity> createEntity(
    configuration: EntityConfiguration
): T {
    try {
        val entityClass = Class.forName(configuration.javaClass)
        entityCounter++
        val dependencies =
            if (SpriteEntity::class.java.isAssignableFrom(entityClass)) {
                val entitySprite = Sprite(assetManager.get(configuration.file, Texture::class.java))
                setOf(configuration, entitySprite)
            }
            else setOf(configuration)

        val entity = createEntityInstance<T>(entityClass, dependencies) as T
        getInitializeFunction<T>(configuration, entity)(entity)
        return entity
    } catch (e: Exception) {
        Gdx.app.error("createEntity", "create entity  [${configuration.uniqueName}] failed")
        throw e
    }
}

fun <T : Entity> getInitializeFunction(
    configuration: EntityConfiguration,
    entityLogic: Entity
): T.() -> Unit {
    if (configuration.initOnCreate) return { entityLogic.onInitialize() }
    else return { }
}

@Suppress("NewApi")
private fun <T : Entity> createEntityInstance(
    clazz: Class<*>,
    externalDependency: Set<Any>
): Entity {
    var dependencyMap = toMap(externalDependency)
    var constructorParameters = clazz.constructors[0].parameters;
    val constructParameters = constructorParameters
        .map { it.type }
        .map {
            dependencyInjectionContext.getBeanByClass(it) ?: dependencyMap[it]
        }
        .toTypedArray()

    return clazz.constructors[0].newInstance(*constructParameters) as Entity
}

private fun toMap(set: Set<Any>): Map<Class<*>, Any> {
    val map: MutableMap<Class<*>, Any> = HashMap()
    set.forEach { map[it.javaClass] = it }
    return map
}