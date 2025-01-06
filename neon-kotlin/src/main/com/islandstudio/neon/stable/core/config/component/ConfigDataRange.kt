package com.islandstudio.neon.stable.core.config.component

open class ConfigDataRange<T>(
    val minValue: T,
    val maxValue: T
) {
    /**
     * Data range for Boolean data type. [min = true, max = false]
     *
     * @constructor Create empty Data range boolean
     */
    object DataRangeBoolean: ConfigDataRange<Boolean>(true, false)
}
