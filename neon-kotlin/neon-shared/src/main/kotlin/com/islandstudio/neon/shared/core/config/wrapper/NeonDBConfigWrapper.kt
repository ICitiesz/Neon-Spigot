package com.islandstudio.neon.shared.core.config.wrapper

import com.islandstudio.neon.shared.core.config.component.type.AbstractConfigWrapper
import com.islandstudio.neon.shared.core.config.wrapper.obj.NeonDBConfigObject
import com.islandstudio.neon.shared.core.config.wrapper.property.NeonDBConfigProperty

class NeonDBConfigWrapper: AbstractConfigWrapper<NeonDBConfigObject, NeonDBConfigProperty<*>>(
    NeonDBConfigObject(),
    NeonDBConfigProperty::class
) {

}