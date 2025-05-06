package com.islandstudio.neon.features.neonfeature.gui.button.node

import java.io.Serializable

data class FeatureOptionGuiButtonNode(
    val featureOptionName: String,
    var featureOptionCurrentValue: Any,
    val featureOptionMinScaleFactor: Any?,
    val featureOptionMaxScaleFactor: Any?
): Serializable