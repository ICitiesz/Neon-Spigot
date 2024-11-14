package com.islandstudio.neon.stable.utils.processing

import com.islandstudio.neon.stable.utils.processing.properties.DataTypes

object GeneralInputProcessor {
    fun validateDataType(inputValue: String, dataType: String): Boolean {
        return validateDataType(inputValue, DataTypes.valueOf(dataType.uppercase()))
    }

    fun validateDataType(inputValue: String, dataType: DataTypes): Boolean {
        when (dataType) {
            DataTypes.BOOLEAN -> {
                inputValue.lowercase().toBooleanStrictOrNull()?.let {
                    return true
                }
            }

            DataTypes.DOUBLE -> {
                 inputValue.toDoubleOrNull()?.let {
                    return true
                }
            }

            DataTypes.INTEGER -> {
                inputValue.toIntOrNull()?.let {
                    return true
                }
            }

            DataTypes.STRING -> {
                return true
            }
        }

        return false
    }

    fun validateDataRange(inputValue: Any, dataType: String, minValue: Any?, maxValue: Any?): Boolean {
        return validateDataRange(inputValue, DataTypes.valueOf(dataType.uppercase()), minValue, maxValue)
    }

    fun validateDataRange(inputValue: Any, dataType: DataTypes, minValue: Any?, maxValue: Any?): Boolean {
        when(dataType) {
            DataTypes.BOOLEAN -> {
                return inputValue.toString().lowercase().toBooleanStrictOrNull()?.let {
                    true
                } ?: false
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

            else -> {
                return false
            }
        }
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
        }
    }
}