package com.islandstudio.neon.shared.core.exception

enum class ExceptionSearchMessages(vararg val message: String) {
    TomlParseExceptionIncorrectFormat("Incorrect format of Key-Value pair (missing equals sign)", "<key = value>"),
    TomlParseExceptionStringValueNotWrapped(" According to the TOML specification string values (even Enums) should be wrapped (start and end) with quotes (\"\")"),
    TomlParseExceptionInvalidSpaces("Not able to parse the key:", "as it has invalid spaces")
    ;

    companion object {
        fun validateExceptionMessage(exceptionMessage: String, exceptionSearchMessages: ExceptionSearchMessages): Boolean {
            return exceptionSearchMessages.message.all { it in exceptionMessage }
        }
    }
}