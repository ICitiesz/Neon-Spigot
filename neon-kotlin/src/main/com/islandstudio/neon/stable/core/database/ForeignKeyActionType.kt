package com.islandstudio.neon.stable.core.database

enum class ForeignKeyActionType {
    /* On delete action types */
    ON_DELETE_NO_ACTION,
    ON_DELETE_SET_DEFAULT,
    ON_DELETE_SET_NULL,
    ON_DELETE_CASCADE,
    ON_DELETE_RESTRICT,

    /* On update action type */
    ON_UPDATE_NO_ACTION,
    ON_UPDATE_SET_DEFAULT,
    ON_UPDATE_SET_NULL,
    ON_UPDATE_CASCADE,
    ON_UPDATE_RESTRICT
}