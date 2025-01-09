package com.islandstudio.neon.shared.core.config

import com.islandstudio.neon.shared.core.config.component.type.AbstractConfigWrapper
import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.component.type.IConfigProperty

class AppConfig<T: AbstractConfigWrapper<U, V>, U: IConfigObject, V: IConfigProperty> {
}