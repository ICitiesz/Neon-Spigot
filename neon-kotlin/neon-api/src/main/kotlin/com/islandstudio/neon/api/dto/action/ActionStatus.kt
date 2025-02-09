package com.islandstudio.neon.api.dto.action

enum class ActionStatus {
    /* General action status */
    SUCCESS,
    FAILURE,

    RECORD_NOT_EXIST,
    DUPLICATE_RECORD,
    NULL_OR_EMPTY_FIELD,

    PLAYER_ROLE_NOT_ASSIGN,
    PLAYER_NOT_EXIST,

    ROLE_NOT_EXIST,
    PERMISSION_NOT_EXIST,
    ROLE_PERMISSION_NOT_EXIST,
    PARENT_PERMISSION_NOT_EXIST,
    PARENT_ROLE_PERMISSION_NOT_EXIST
}