package com.islandstudio.neon.shared.utils.data


object DataUtil {
    fun validateDataType(value: Any, dataType: DataType): Boolean {
        return validateDataType(value.toString(), dataType)
    }

    fun validateDataType(value: String, dataType: String): Boolean {
        return validateDataType(value, DataType.valueOf(dataType))
    }

    fun validateDataType(value: String, dataType: DataType): Boolean {
         when (dataType) {
            DataType.Boolean -> {
                value.lowercase().toBooleanStrictOrNull()?.let {
                    return true
                }
            }

            DataType.Double -> {
                value.toDoubleOrNull()?.let {
                    return true
                }
            }

            DataType.Integer -> {
                value.toIntOrNull()?.let {
                    return true
                }
            }

            DataType.Long -> {
                value.toLongOrNull()?.let {
                    return true
                }
            }

            DataType.String -> {
                return true
            }

            else -> {
                return false
            }
        }

        return false
    }

    fun validateDataRange(inputValue: Any, dataType: String, minValue: Any? = null, maxValue: Any? = null): Boolean {
        return validateDataRange(inputValue, DataType.valueOf(dataType), minValue, maxValue)
    }

    fun validateDataRange(inputValue: Any, dataType: DataType, minValue: Any? = null, maxValue: Any? = null): Boolean {
        when(dataType) {
            DataType.Boolean  -> {
                inputValue.toString().lowercase().toBooleanStrictOrNull()?.let { return true } ?: return false
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

        return false
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
}