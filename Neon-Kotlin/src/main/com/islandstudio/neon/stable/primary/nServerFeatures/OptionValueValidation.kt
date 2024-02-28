package com.islandstudio.neon.stable.primary.nServerFeatures

object OptionValueValidation {
    enum class DataTypes(val dataType: String) {
        BOOLEAN("Boolean"),
        DOUBLE("Double"),
        INTEGER("Integer"),
        STRING("String")
    }

    /**
     * Validate data range of option value. Return true if data range is valid else return false.
     *
     * @param inputValue The option value.
     * @param dataType Data type of option value.
     * @return True / False
     */
    fun isDataRangeValid(inputValue: Any, dataType: String, dataRange: Array<String>): Boolean {
        val minValue: Any // Minimum value for specific option.
        val maxValue: Any // Maximum value for specific option.

        when (dataType) {
            "Double" -> {
                minValue = dataRange.first().toDouble()
                maxValue = dataRange.last().toDouble()

                val doubleValue = if (inputValue::class.java.simpleName.equals(DataTypes.INTEGER.dataType)) (inputValue as Int).toDouble() else inputValue as Double

                return !(doubleValue < minValue || doubleValue > maxValue)
            }

            "Integer" -> {
                minValue = dataRange.first().toInt()
                maxValue = dataRange.last().toInt()

                val integerValue = inputValue as Int

                return !(integerValue < minValue || integerValue > maxValue)
            }

            "Boolean" -> {
                return inputValue is Boolean
            }
        }

        return false
    }

    /**
     * Validate data type of option value. Return the value if data type is valid else return null.
     *
     * @param dataType Data type of option value.
     * @param inputValue The option value.
     * @return The option value if data type is valid else return null.
     */
    fun isDataTypeValid(dataType: String, inputValue: String?): Any? {
        when (dataType) {
            DataTypes.BOOLEAN.dataType -> {
                return inputValue?.lowercase()?.toBooleanStrictOrNull()
            }

            DataTypes.DOUBLE.dataType -> {
                return inputValue?.toDoubleOrNull()
            }

            DataTypes.INTEGER.dataType -> {
                return inputValue?.toIntOrNull()
            }

            else -> {
                return inputValue
            }
        }
    }
}