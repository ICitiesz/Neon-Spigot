package com.islandstudio.neon.experimental.nFireworks

import com.islandstudio.neon.stable.core.application.server.NPacketProcessor
import org.bukkit.World
import org.bukkit.scheduler.BukkitRunnable

class DisplayPixel(private val pixel: Pixel, private val world: World) : BukkitRunnable() {
    private val durationInTicks = (2 * 20)
    private var currentTicks = 0

    override fun run() {
        NPacketProcessor.getNWorld(world).sendParticles(null, pixel.dustParticleOptions,
                        pixel.explodeLocationX, pixel.explodeLocationY, pixel.explodeLocationZ, 1, 0.0, 0.0, 0.0, 1.0, true)

        currentTicks++

        if (currentTicks >= durationInTicks) {
            this.cancel()
        }
    }
}