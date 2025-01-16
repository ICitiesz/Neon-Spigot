package com.islandstudio.neon.stable.common.action

enum class ActionStatus {
    /* General Action Status Types */
    UNKNOWN,
    SUCCESS,
    EMPTY_STRING,
    INVALID_COMMAND_ARGUMENT,
    INVALID_STRING_LENGTH,
    INVALID_STRING_PATTERN,
    INVALID_CONFIRMATION,
    DUPLICATE_RECORD,
    UNDELETABLE_RECORD,
    NOT_UPDATABLE_RECORD,
    RECORD_NOT_EXIST,
    EMPTY_COLLECTION,
    SERVER_FEATURE_FAILED_TO_RELOAD,


    /* nRole Action Types */
    ROLE_NOT_EXIST,

    /* nAccessPermission Action Types */
    PERMISSION_NOT_EXIST,

    /* nPlayerProfile Action Types */
    PLAYER_ROLE_IDENTICAL,
    PLAYER_ROLE_NOT_ASSIGN,
}