package com.islandstudio.neon.stable.core.application.datakey

import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.core.io.resource.ResourceManager
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.util.*

@Single
class DataKeyManager {
    private val dataKeyProperties = Properties()

    companion object: IRunner, IComponentInjector {
        private val dataKeyManager by inject<DataKeyManager>()

        override fun run() {
            dataKeyManager.dataKeyProperties.load(
                ResourceManager.getNeonResourceAsStream(
                    NeonInternalResource.NeonGeneralDataKeyProperties
                )
            )

            dataKeyManager.dataKeyProperties.load(
                ResourceManager.getNeonResourceAsStream(
                    NeonInternalResource.NeonRecipeDataKeyProperties
                )
            )
        }
    }

    fun fromProperty(keyName: String): String {
        return dataKeyProperties.getProperty(keyName)
    }
}