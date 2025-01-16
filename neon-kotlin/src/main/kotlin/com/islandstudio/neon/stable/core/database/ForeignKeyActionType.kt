package com.islandstudio.neon.stable.core.database

enum class ForeignKeyActionType {
    /* On delete action types */
    ON_DELETE_NO_ACTION, // Short form: DNA
    ON_DELETE_SET_DEFAULT, // DSD
    ON_DELETE_SET_NULL, // DSN
    ON_DELETE_CASCADE, // DC
    ON_DELETE_RESTRICT, // DR

    /* On update action type */
    ON_UPDATE_NO_ACTION, // UNA
    ON_UPDATE_SET_DEFAULT, // USD
    ON_UPDATE_SET_NULL, // USN
    ON_UPDATE_CASCADE, // UC
    ON_UPDATE_RESTRICT // UR


    // TODO: Need create function to parse the short form from the constraint name
}