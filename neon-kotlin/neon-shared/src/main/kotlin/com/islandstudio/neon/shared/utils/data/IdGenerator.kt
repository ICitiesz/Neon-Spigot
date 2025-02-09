package com.islandstudio.neon.shared.utils.data

import com.github.f4b6a3.ulid.UlidCreator

object IdGenerator {
    fun generatePlayerSessionId(): String {
        return UlidCreator.getUlid().toLowerCase()
    }
}