package com.islandstudio.neon.api.repository.system

interface IDatabaseInfoRepository {
    fun existConnected(): Boolean
}