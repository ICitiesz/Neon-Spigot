package com.islandstudio.neon.rework.core.datakey

import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.rework.core.di.IComponentProvider
import com.islandstudio.neon.shared.rework.core.di.getComponent
import com.islandstudio.neon.shared.rework.core.initialization.context.IPluginContext
import com.islandstudio.neon.shared.rework.core.initialization.runner.IRunnerAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.util.*

@Single
class DataKeyManager {
    private val dataKeyProperties = Properties()

    companion object: IRunnerAsync, IComponentProvider {
        private val pluginContext = getComponent<IPluginContext>()
        private val dataKeyManager = getComponent<DataKeyManager>()
        private val resourceManager = pluginContext.resourceManager

        override suspend fun runSuspend() {
            withContext(Dispatchers.IO) {
                pluginContext.getPluginLogger().info("Initializing data key manager...")

                dataKeyManager.dataKeyProperties.load(
                    resourceManager.getNeonResourceAsStream(
                        NeonInternalResource.NeonGeneralDataKeyProperties
                    )
                )

                dataKeyManager.dataKeyProperties.load(
                    resourceManager.getNeonResourceAsStream(
                        NeonInternalResource.NeonRecipeDataKeyProperties
                    )
                )
            }
        }
    }

    fun fromProperty(keyName: String): String {
        return dataKeyProperties.getProperty(keyName)
    }
}