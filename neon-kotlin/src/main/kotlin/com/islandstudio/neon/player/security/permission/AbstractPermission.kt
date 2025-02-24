package com.islandstudio.neon.player.security.permission

abstract class AbstractPermission(val mainPermission: AbstractPermission?) {
    abstract val permissionCode: String
    abstract val description: String
}