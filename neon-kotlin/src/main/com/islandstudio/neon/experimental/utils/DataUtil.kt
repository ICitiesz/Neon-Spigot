package com.islandstudio.neon.experimental.utils

import com.islandstudio.neon.stable.utils.processing.properties.DataTypes

object DataUtil {
    fun validateDataType(value: Any, dataType: DataTypes): Boolean {
        return validateDataType(value.toString(), dataType)
    }

    fun validateDataType(value: String, dataType: String): Boolean {
        return validateDataType(value, DataTypes.valueOf(dataType.uppercase()))
    }

    fun validateDataType(value: String, dataType: DataTypes): Boolean {
        when (dataType) {
            DataTypes.BOOLEAN -> {
                value.lowercase().toBooleanStrictOrNull()?.let {
                    return true
                }
            }

            DataTypes.DOUBLE -> {
                value.toDoubleOrNull()?.let {
                    return true
                }
            }

            DataTypes.INTEGER -> {
                value.toIntOrNull()?.let {
                    return true
                }
            }

            DataTypes.STRING -> {
                return true
            }

            DataTypes.LONG -> {
                value.toLongOrNull()?.let {
                    return true
                }
            }

            DataTypes.UNSUPPORTED_DATA_TYPE -> {
                return false
            }
        }

        return false
    }

    fun validateDataRange(inputValue: Any, dataType: String, minValue: Any? = null, maxValue: Any? = null): Boolean {
        return validateDataRange(inputValue, DataTypes.valueOf(dataType.uppercase()), minValue, maxValue)
    }

    fun validateDataRange(inputValue: Any, dataType: DataTypes, minValue: Any? = null, maxValue: Any? = null): Boolean {
        when(dataType) {
            DataTypes.BOOLEAN -> {
                inputValue.toString().lowercase().toBooleanStrictOrNull()?.let { return true } ?: return false
            }

            DataTypes.DOUBLE -> {
                val doubleValue = inputValue.toString().toDoubleOrNull() ?: return false

//                val doubleValue = if (inputValue::class.java.simpleName.equals(DataTypes.INTEGER.dataType)) {
//                    (inputValue as Int).toDouble()
//                } else {
//                    inputValue as Double
//                }

                return !(doubleValue < minValue.toString().toDouble() || doubleValue > maxValue.toString().toDouble())
            }

            DataTypes.INTEGER -> {
                val integerValue =  inputValue.toString().toIntOrNull() ?: return false

//                val integerValue =  if (inputValue::class.java.simpleName.equals(DataTypes.STRING.dataType)) {
//                    inputValue.toString().toInt()
//                } else {
//                    inputValue as Int
//                }

                return !(integerValue < minValue.toString().toInt() || integerValue > maxValue.toString().toInt())
            }

            DataTypes.LONG -> {
                val longValue = inputValue.toString().toLongOrNull() ?: return false

                return !(longValue < minValue.toString().toLong() || longValue > maxValue.toString().toLong())
            }

            else -> {
                return false
            }
        }

        return false
    }

    fun convertDataType(inputValue: Any, dataType: DataTypes): Any? {
        return convertDataType(inputValue.toString(), dataType)
    }

    fun convertDataType(inputValue: String, dataType: String): Any? {
        return convertDataType(inputValue, DataTypes.valueOf(dataType.uppercase()))
    }

    fun convertDataType(inputValue: String, dataType: DataTypes): Any? {
        when (dataType) {
            DataTypes.BOOLEAN -> {
                return inputValue.lowercase().toBooleanStrictOrNull()
            }

            DataTypes.DOUBLE -> {
                return inputValue.toDoubleOrNull()
            }

            DataTypes.INTEGER -> {
                return inputValue.toIntOrNull()
            }

            DataTypes.STRING -> {
                return inputValue
            }

            DataTypes.LONG -> {
                return inputValue.toLongOrNull()
            }

            DataTypes.UNSUPPORTED_DATA_TYPE -> return null
        }
    }
}