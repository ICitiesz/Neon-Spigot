package com.islandstudio.neon.experimental.nServerConfigurationNew

import kotlin.math.max

object OptionDataValidation {
    /**
     * Validate data type of option value. Return the value if data type is valid else return null.
     *
     * @param dataType Data type of option value.
     * @param inputValue The option value.
     * @return The option value if data type is valid else return null.
     */
    fun isDataTypeValid(dataType: DataTypes, inputValue: String?): Any? {
        when (dataType) {
            DataTypes.BOOLEAN -> {
                return inputValue?.toBooleanStrictOrNull()
            }

            DataTypes.FLOAT -> {
                return inputValue?.toFloatOrNull()
            }

            DataTypes.DOUBLE -> {
                return inputValue?.toDoubleOrNull()
            }

            DataTypes.BYTE -> {
                return inputValue?.toByteOrNull()
            }

            DataTypes.SHORT -> {
                return inputValue?.toShortOrNull()
            }

            DataTypes.INTEGER -> {
                return inputValue?.toIntOrNull()
            }

            else -> {
                return inputValue?.toLongOrNull()
            }
        }
    }

    /**
     * Validate data range of option value. Return true if data range is valid else return false.
     *
     * @param dataType Data type of option value.
     * @param dataRange Data range of option value.
     * @param inputValue The option value.
     * @return True if data range is valid else return false.
     */
    fun isDataRangeValid(dataType: DataTypes, dataRange: List<String>, inputValue: Any?): Boolean {
        val minValue: Any // Minimum value for specific option.
        val maxValue: Any // Maximum value for specific option.

        when (dataType) {
            DataTypes.FLOAT -> {
                minValue = dataRange[0].toFloat()
                maxValue = dataRange[1].toFloat()

                if ((inputValue as Float) < minValue || inputValue > maxValue) return false
            }

            DataTypes.DOUBLE -> {
                minValue = dataRange[0].toDouble()
                maxValue = dataRange[1].toDouble()

                if ((inputValue as Double) < minValue || inputValue > maxValue) return false
            }

            DataTypes.BYTE -> {
                minValue = dataRange[0].toByte()
                maxValue = dataRange[1].toByte()

                if ((inputValue as Byte) < minValue || inputValue > maxValue) return false
            }

            DataTypes.SHORT -> {
                minValue = dataRange[0].toShort()
                maxValue = dataRange[1].toShort()

                if ((inputValue as Short) < minValue || inputValue > maxValue) return false
            }

            DataTypes.INTEGER -> {
                minValue = dataRange[0].toInt()
                maxValue = dataRange[1].toInt()

                if ((inputValue as Int) < minValue || inputValue > maxValue) return false
            }

            else -> {
                minValue = dataRange[0].toLong()
                maxValue = dataRange[1].toLong()

                if ((inputValue as Long) < minValue || inputValue > maxValue) return false
            }
        }

        return true
    }

    enum class DataTypes(val dataType: String) {
        BOOLEAN("Boolean"),
        FLOAT("Float"),
        DOUBLE("Double"),
        BYTE("Byte"),
        SHORT("Short"),
        INTEGER("Integer"),
        LONG("Long"),
        STRING("String")
    }
}