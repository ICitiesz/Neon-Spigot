package com.islandstudio.neon.experimental.nServerFeaturesBeta

enum class ServerFeatureOptionProperties(val option: String, val dataType: OptionDataValidation.DataTypes, val dataRange: List<String>) {
    NWAYPOINTS_CROSS_DIMENSION("nWaypoints.option.cross_dimension", OptionDataValidation.DataTypes.BOOLEAN, DataRange.booleanRange),
    FEATURE_NAME_6_LEVEL("featureName6.option.level", OptionDataValidation.DataTypes.INTEGER, DataRange.getNumberRange("1", "10")),
    FEATURENAME7_OPTION_1("featureName7.option.level", OptionDataValidation.DataTypes.BOOLEAN, DataRange.booleanRange);
}

private object DataRange {
    val booleanRange: List<String> = listOf("true", "false")

    fun getNumberRange(number_1: String, number_2: String): List<String> {
        return listOf(number_1, number_2)
    }
}