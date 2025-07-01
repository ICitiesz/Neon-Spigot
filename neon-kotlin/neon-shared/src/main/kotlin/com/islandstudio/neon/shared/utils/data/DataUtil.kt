package com.islandstudio.neon.shared.utils.data

import com.islandstudio.neon.shared.core.exception.NeonException
import kotlin.math.pow


object DataUtil {
    fun validateDataType(value: Any, dataType: DataType): Boolean {
        return validateDataType(value.toString(), dataType)
    }

    fun validateDataType(value: String, dataType: String): Boolean {
        return validateDataType(value, DataType.valueOf(dataType))
    }

    fun validateDataType(value: String, dataType: DataType): Boolean {
         return when (dataType) {
            DataType.Boolean -> {
                value.lowercase().toBooleanStrictOrNull()?.let { true } ?: false
            }

            DataType.Double -> {
                value.toDoubleOrNull()?.let { true } ?: false
            }

            DataType.Integer -> {
                value.toIntOrNull()?.let { true } ?: false
            }

            DataType.Long -> {
                value.toLongOrNull()?.let { true } ?: false
            }

            DataType.String -> true

            else -> false
        }
    }

    fun validateDataRange(inputValue: Any, dataType: String, minValue: Any? = null, maxValue: Any? = null): Boolean {
        return validateDataRange(inputValue, DataType.valueOf(dataType), minValue, maxValue)
    }

    fun validateDataRange(inputValue: Any, dataType: DataType, minValue: Any? = null, maxValue: Any? = null): Boolean {
        when(dataType) {
            DataType.Boolean  -> {
                return inputValue.toString().lowercase().toBooleanStrictOrNull()?.let { true } ?: false
            }

            DataType.Double -> {
                val doubleValue = inputValue.toString().toDoubleOrNull() ?: return false

//                val doubleValue = if (inputValue::class.java.simpleName.equals(DataTypes.INTEGER.dataType)) {
//                    (inputValue as Int).toDouble()
//                } else {
//                    inputValue as Double
//                }

                return !(doubleValue < minValue.toString().toDouble() || doubleValue > maxValue.toString().toDouble())
            }

            DataType.Integer -> {
                val integerValue =  inputValue.toString().toIntOrNull() ?: return false

//                val integerValue =  if (inputValue::class.java.simpleName.equals(DataTypes.STRING.dataType)) {
//                    inputValue.toString().toInt()
//                } else {
//                    inputValue as Int
//                }

                return !(integerValue < minValue.toString().toInt() || integerValue > maxValue.toString().toInt())
            }

            DataType.Long -> {
                val longValue = inputValue.toString().toLongOrNull() ?: return false

                return !(longValue < minValue.toString().toLong() || longValue > maxValue.toString().toLong())
            }

            else -> {
                return false
            }
        }
    }

    fun convertDataType(inputValue: Any, dataType: DataType): Any? {
        return convertDataType(inputValue.toString(), dataType)
    }

    fun convertDataType(inputValue: String, dataType: String): Any? {
        return convertDataType(inputValue, DataType.valueOf(dataType.uppercase()))
    }

    fun convertDataType(inputValue: String, dataType: DataType): Any? {
        when (dataType) {
            DataType.Boolean -> {
                return inputValue.lowercase().toBooleanStrictOrNull()
            }

            DataType.Double -> {
                return inputValue.toDoubleOrNull()
            }

            DataType.Integer -> {
                return inputValue.toIntOrNull()
            }

            DataType.String -> {
                return inputValue
            }

            DataType.Long -> {
                return inputValue.toLongOrNull()
            }

            else -> return null
        }
    }

    /**
     * Get scale factor of Double value
     *
     * @param value The given Double value
     * @param isMaxScaleFactor Determine whether the calculated scale factor should be the maximum.
     * @return
     */
    fun getScaleFactorOfDouble(value: Double, isMaxScaleFactor: Boolean = false): Double {
        return getFloatingPointCount(value).run {
            with(1.0 / 10.0.pow(this.toDouble())) {
                if (isMaxScaleFactor) return@with this * 10

                this
            }
        }
    }

    /**
     * Get floating point count of a Double value
     *
     * @param value The given Double value
     * @return
     */
    fun getFloatingPointCount(value: Double): Int {
        return value.toString().substringAfter(".").length
    }

    fun roundOffDouble(value: Double, floatingPointCount: Int): Double {
        return String.format("%.${floatingPointCount}f", value).toDouble()
    }

    inline fun <reified T> asType(value: Any): T {
        if (value !is T) throw NeonException("Error while trying to cast value as ${T::class.java.name}!")

        return value
    }
}