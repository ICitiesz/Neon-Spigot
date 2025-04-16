package com.islandstudio.neon.shared.core.config.property.custom

import com.islandstudio.neon.shared.core.config.component.type.IConfigProperty

data class NeonFeatureConfigCustomData(
    val featureDescription: String,
    val isExperimental: Boolean = false
): IConfigProperty