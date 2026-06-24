package com.islandstudio.neon.shared.rework.core.config.component

import com.islandstudio.neon.shared.rework.core.io.resource.ExternalResource
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

abstract class ConfigObjectWrapper<T> {
    abstract val defaultInstance: T
    abstract val configSections: List<AbstractConfigSection>
    abstract val externalResource: ExternalResource

    protected fun <V: Any> getAllConfigSection(clazz: KClass<V>): List<AbstractConfigSection> {
        return clazz.sealedSubclasses
            .filter { it.isSubclassOf(AbstractConfigSection::class) }
            .mapNotNull { it.objectInstance as? AbstractConfigSection }
            .toList()
    }
}