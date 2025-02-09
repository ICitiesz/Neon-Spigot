package com.islandstudio.neon.stable.player.authorization

abstract class AbstractPermission {
    abstract val permissionCode: String
    abstract val description: String
    open val parentPermission: AbstractPermission? = null
}