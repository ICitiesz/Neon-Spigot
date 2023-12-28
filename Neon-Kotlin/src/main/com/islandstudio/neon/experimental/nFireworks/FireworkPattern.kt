package com.islandstudio.neon.experimental.nFireworks

import com.islandstudio.neon.stable.core.init.NConstructor
import com.islandstudio.neon.stable.core.network.NPacketProcessor
import kotlinx.coroutines.*
import net.minecraft.core.particles.DustParticleOptions
import org.bukkit.Bukkit
import org.bukkit.Location
import org.joml.Vector3f
import java.awt.Color
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

object FireworkPattern {
    data class PixelContainer(val verticalIndex: Int): Serializable {
        val horizontalPixels: ArrayList<Pixel> = ArrayList()

        /**
         * Render horizontal pixels
         *
         * @param explodePos The exploded firework position.
         * @param verticalPointer The vertical pointer
         * @param renderDuration The render duration in seconds.
         */
        @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
        fun renderHorizontalPixel(explodePos: Location, verticalPointer: Double, renderDuration: Int) {
            val defaultTickRate = AtomicInteger(20)
            val renderDurationInTicks = AtomicInteger(renderDuration * defaultTickRate.get())
            val durationCounter = AtomicInteger(0)

            Bukkit.getServer().scheduler.runTaskTimer(NConstructor.plugin, { bukkitTask ->
                var horizontalPixelWorker: Job? = CoroutineScope(newSingleThreadContext("nFireworks Horizontal Pixel Worker"))
                    .launch {
                        val horizontalStart = String.format("%.1f", explodePos.x - (NFireworks.PATTERN_FRAME_INGAME_SIZE / 2)).toDouble()
                        val horizontalEnd = String.format("%.1f", explodePos.x + (NFireworks.PATTERN_FRAME_INGAME_SIZE / 2)).toDouble()

                        /* Pointer for horizontal pixel where used to locate the current position within the horizontal pixels */
                        var horizontalPointer: Double = String.format("%.1f", horizontalStart).toDouble()

                        horizontalPixels.stream()
                            .sorted { hPixel1, hPixel2 -> hPixel1.horizontalIndex.compareTo(hPixel2.horizontalIndex) }
                            .toList()
                            .asReversed()
                            .forEach {
                                if (horizontalPointer >= horizontalEnd) horizontalPointer = horizontalStart

                                /* Check if the pixelColor is null, if it is, horizontal pointer will move forward */
                                if (it.pixelColor == null) {
                                    horizontalPointer += NFireworks.PATTERN_FRAME_POINTER_INCREMENT
                                    horizontalPointer = String.format("%.1f", horizontalPointer).toDouble()
                                    return@forEach
                                }

                                    val pixelDust = DustParticleOptions(it.pixelPos, 2.0F)

                                /* Render the pixel */
                                NPacketProcessor.getNWorld(explodePos.world!!).sendParticles(
                                    null, pixelDust, horizontalPointer, verticalPointer,
                                    explodePos.z, 1, 0.0, 0.0, 0.0, 1.0, true
                                )

                                horizontalPointer += NFireworks.PATTERN_FRAME_POINTER_INCREMENT
                                horizontalPointer = String.format("%.1f", horizontalPointer).toDouble()
                            }
                }

                /* Destroy the worker once the task has completed */
                horizontalPixelWorker?.invokeOnCompletion { horizontalPixelWorker = null }

                /* TODO: Customizable render duration */
                /* Main problem: Worker may not release after complete */
                if (durationCounter.get() >= renderDurationInTicks.get()) {
                    bukkitTask.cancel()
                }

                durationCounter.getAndAdd(20)
            }, 0L, 5L)
        }
    }

    data class Pixel(val horizontalIndex: Int, val pixelColor: Color? = null): Serializable {
        val pixelPos = pixelColor?.let {
            Vector3f((it.red.toFloat()) / 255, (it.green.toFloat()) / 255, (it.blue.toFloat()) / 255)
        }
    }
}


