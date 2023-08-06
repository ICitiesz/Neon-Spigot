package com.islandstudio.neon.experimental.nFireworks

import com.mojang.math.Vector3f
import net.minecraft.core.particles.DustParticleOptions
import org.bukkit.Location
import java.awt.Color

data class Pixel(val pixelColor: Color, val pixelLocation: Location) {
    val dustParticleOptions = DustParticleOptions(
        Vector3f(
            (pixelColor.red.toFloat()) / 255,
            (pixelColor.green.toFloat()) / 255,
            (pixelColor.blue.toFloat()) / 255
        ),
        0.9F
    )

    val explodeLocationX = pixelLocation.x
    val explodeLocationY = pixelLocation.y
    val explodeLocationZ = pixelLocation.z
}
