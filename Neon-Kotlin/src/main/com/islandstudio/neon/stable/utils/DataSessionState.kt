package com.islandstudio.neon.stable.utils

enum class DataSessionState {
    /* Internal Data State is from internal resources, it's readable only, where it used as reference to update external resources. */
    INTERNAL,

    /* External Data State is from external resources, it's readable only, where it used as reference to toggle all sort of features
    * during the plugin initialization stage.
    */
    EXTERNAL,

    /* Editable Data State is from external resources, it's readable and writable, where it used as reference to modify value from
    * external resources without actually modify the external resources. */
    EDITABLE
}