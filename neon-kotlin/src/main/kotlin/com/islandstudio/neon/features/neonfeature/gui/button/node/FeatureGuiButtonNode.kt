package com.islandstudio.neon.features.neonfeature.gui.button.node

import java.io.Serializable

data class FeatureGuiButtonNode(
    val featureName: String,
    val isExperimental: Boolean
): Serializable