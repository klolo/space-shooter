package pl.klolo.spaceshooter.game.engine

class DependencyInjectionContext {

    private val beanRegistry: MutableMap<Class<*>, Any> = mutableMapOf()

    fun <T> registerBean(classRef: Class<T>) {
        println("Register: $classRef")
        val constructParameter = classRef.constructors[0].parameterTypes
            .map { getBeanByClass(it) }
            .filterNotNull()
            .toTypedArray()

        beanRegistry[classRef] = classRef.constructors[0].newInstance(*constructParameter)
    }

    fun registerBean(instance: Any) {
        println("Register: $instance")
        beanRegistry[instance.javaClass] = instance
    }

    fun getBeanByClass(classRef: Class<*>): Any? {
        if (classRef.isInterface) {
            return beanRegistry
                .filter { it.key.interfaces.contains(classRef) }
                .map { it.value }
                .first()
        }

        return beanRegistry[classRef]
    }
}