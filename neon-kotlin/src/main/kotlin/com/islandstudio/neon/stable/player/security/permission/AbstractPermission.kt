package com.islandstudio.neon.stable.player.security.permission

abstract class AbstractPermission(val mainPermission: AbstractPermission?) {
    abstract val permissionCode: String
    abstract val description: String
}