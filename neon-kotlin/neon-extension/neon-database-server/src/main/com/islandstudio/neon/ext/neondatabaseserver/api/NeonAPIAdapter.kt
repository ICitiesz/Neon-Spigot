package com.islandstudio.neon.ext.neondatabaseserver.api

import com.islandstudio.neon.api.IAPIAdapter
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.koin.core.annotation.Single

@Single
class NeonAPIAdapter: IComponentInjector, IAPIAdapter {
}