package pl.klolo.game.entity

enum class EntityType {
    SPRITE_WITH_LOGIC,
    SPRITE_WITH_CUSTOM_RENDERING,
    ENTITY_WITH_LOGIC,
    TEXT_ENTITY,
    PARTICLE_ENTITY
}

data class EntityConfiguration(
        /** human readable name - required */
        val uniqueName: String,

        /** entity type - required */
        val type: EntityType,

        /** full package name of EntityLogic class implementation */
        val logicClass: String = "",

        /** File used by entity */
        val file: String = "",

        /** Position of the entity */
        val x: Float = 0f,

        /** Position of the entity */
        val y: Float = 0f,

        /** Size of the entity */
        val width: Float = 0f,

        /** Size of the entity */
        val height: Float = 0f,

        /** Order on stage */
        var layer: Int = 1,

        /** If true initializeMoving function on logic is called on loading game */
        var initOnCreate: Boolean = false,

        /** Depends on this entity is render with or without light */
        var useLighting: Boolean = true
)