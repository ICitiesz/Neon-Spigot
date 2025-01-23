package com.islandstudio.neon.api.dto.action

import com.islandstudio.neon.shared.core.exception.NeonException

interface IActionResult<T> {
    val result: T?
    val displayMessage: String?
    val logMessage: String?
    val status: ActionStatus
    val neonException: NeonException?
}