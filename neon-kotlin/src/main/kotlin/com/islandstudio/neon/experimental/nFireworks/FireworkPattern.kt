package com.islandstudio.neon.experimental.nFireworks

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.core.application.server.ServerGamePacketManager
import kotlinx.coroutines.*
import net.minecraft.core.particles.DustParticleOptions
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.joml.Vector3f
import org.koin.core.component.inject
import java.awt.Color
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

object FireworkPattern {
    data class PixelContainer(val verticalIndex: Int): Serializable, IComponentInjector {
        val horizontalPixels: ArrayList<Pixel> = ArrayList()

        /**
         * Render horizontal pixels
         *
         * @param explodePos The exploded firework position.
         * @param verticalPointer The vertical pointer
         * @param renderDuration The render duration in seconds.
         */
        @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
        fun renderHorizontalPixel(explodePos: Location, verticalPointer: Double, renderDuration: Int, fireworkPatternFacing: BlockFace) {
            val neon by inject<Neon>()
            val defaultTickRate = AtomicInteger(20)
            val renderDurationInTicks = AtomicInteger(renderDuration * defaultTickRate.get())
            val durationCounter = AtomicInteger(0)

            Bukkit.getServer().scheduler.runTaskTimer(neon, { bukkitTask ->
                var horizontalPixelWorker: Job? =
                    CoroutineScope(newSingleThreadContext("nFireworks Horizontal Pixel Worker"))
                        .launch {
                            /* Firework pattern facing */
                            val fireworkPatternFacingModX = fireworkPatternFacing.modX
                            val fireworkPatternFacingModZ = fireworkPatternFacing.modZ

                            /*
                            * If -Z then:
                            * H.Start = explodePosX - (PATTERN_SIZE / 2)
                            * H.End = explodePosX + (PATTERN_SIZE / 2)
                            *
                            * If Z+ then:
                            * H.Start = explodePosX + (PATTERN_SIZE / 2)
                            * H.End = explodePosX - (PATTERN_SIZE / 2)
                            *
                            * If X+ then:
                            * H.Start = explodePosZ - (PATTERN_SIZE / 2)
                            * H.End = explodePosZ + (PATTERN_SIZE / 2)
                            *
                            * If -X then:
                            * H.Start = explodePosZ + (PATTERN_SIZE / 2)
                            * H.End = explodePosZ - (PATTERN_SIZE / 2)
                            *
                            *  */

                            when {
                                /*
                                * (-X) H.Start <--> H.End (X+)
                                *
                                *         ^ Facing -Z ^
                                *
                                * */
                                (fireworkPatternFacingModZ == -1) -> {
                                    val horizontalStart = String.format("%.1f", explodePos.x - (NFireworks.PATTERN_FRAME_INGAME_SIZE / 2)).toDouble()
                                    val horizontalEnd = String.format("%.1f", explodePos.x + (NFireworks.PATTERN_FRAME_INGAME_SIZE / 2)).toDouble()

                                    /* Pointer for horizontal pixel where used to locate the current position within the horizontal pixels */
                                    var horizontalPointer: Double = String.format("%.1f", horizontalStart).toDouble()

                                    horizontalPixels.stream()
                                        .sorted { hPixel1, hPixel2 -> hPixel1.horizontalIndex.compareTo(hPixel2.horizontalIndex) }
                                        .toList()
                                        .forEach {
                                            if (horizontalPointer >= horizontalEnd) horizontalPointer = horizontalStart

                                            /* Check if the pixelColor is null, if it is, horizontal pointer will move forward */
                                            if (it.pixelColor == null) {
                                                horizontalPointer += NFireworks.PATTERN_FRAME_POINTER_INCREMENT_DECREMENT
                                                horizontalPointer = String.format("%.1f", horizontalPointer).toDouble()
                                                return@forEach
                                            }

                                            val pixelDust = DustParticleOptions(it.pixelPos, 0.9F)

                                            /* Render the pixel */
                                            ServerGamePacketManager.getMcWorld(explodePos.world!!).sendParticles(
                                                null, pixelDust, horizontalPointer, verticalPointer,
                                                explodePos.z, 1, 0.0, 0.0, 0.0, 1.0, true
                                            )

                                            horizontalPointer += NFireworks.PATTERN_FRAME_POINTER_INCREMENT_DECREMENT
                                            horizontalPointer = String.format("%.1f", horizontalPointer).toDouble()
                                        }
                                }

                                /*
                                * (X+) H.Start <--> H.End (-X)
                                *
                                *         ^ Facing Z+ ^
                                *
                                * */
                                (fireworkPatternFacingModZ == 1) -> {
                                    val horizontalStart = String.format("%.1f", explodePos.x + (NFireworks.PATTERN_FRAME_INGAME_SIZE / 2)).toDouble()
                                    val horizontalEnd = String.format("%.1f", explodePos.x - (NFireworks.PATTERN_FRAME_INGAME_SIZE / 2)).toDouble()

                                    /* Pointer for horizontal pixel where used to locate the current position within the horizontal pixels */
                                    var horizontalPointer: Double = String.format("%.1f", horizontalStart).toDouble()

                                    horizontalPixels.stream()
                                        .sorted { hPixel1, hPixel2 -> hPixel1.horizontalIndex.compareTo(hPixel2.horizontalIndex) }
                                        .toList()
                                        .forEach {
                                            if (horizontalPointer <= horizontalEnd) horizontalPointer = horizontalStart

                                            /* Check if the pixelColor is null, if it is, horizontal pointer will move forward */
                                            if (it.pixelColor == null) {
                                                horizontalPointer -= NFireworks.PATTERN_FRAME_POINTER_INCREMENT_DECREMENT
                                                horizontalPointer = String.format("%.1f", horizontalPointer).toDouble()
                                                return@forEach
                                            }

                                            val pixelDust = DustParticleOptions(it.pixelPos, 0.9F)

                                            /* Render the pixel */
                                            ServerGamePacketManager.getMcWorld(explodePos.world!!).sendParticles(
                                                null, pixelDust, horizontalPointer, verticalPointer,
                                                explodePos.z, 1, 0.0, 0.0, 0.0, 1.0, true
                                            )

                                            horizontalPointer -= NFireworks.PATTERN_FRAME_POINTER_INCREMENT_DECREMENT
                                            horizontalPointer = String.format("%.1f", horizontalPointer).toDouble()
                                        }
                                }

                                /*
                                * (-Z) H.Start <--> H.End (Z+)
                                *
                                *         ^ Facing X+ ^
                                *
                                * */
                                (fireworkPatternFacingModX == 1) -> {
                                    val horizontalStart = String.format("%.1f", explodePos.z - (NFireworks.PATTERN_FRAME_INGAME_SIZE / 2)).toDouble()
                                    val horizontalEnd = String.format("%.1f", explodePos.z + (NFireworks.PATTERN_FRAME_INGAME_SIZE / 2)).toDouble()

                                    /* Pointer for horizontal pixel where used to locate the current position within the horizontal pixels */
                                    var horizontalPointer: Double = String.format("%.1f", horizontalStart).toDouble()

                                    horizontalPixels.stream()
                                        .sorted { hPixel1, hPixel2 -> hPixel1.horizontalIndex.compareTo(hPixel2.horizontalIndex) }
                                        .toList()
                                        .forEach {
                                            if (horizontalPointer >= horizontalEnd) horizontalPointer = horizontalStart

                                            /* Check if the pixelColor is null, if it is, horizontal pointer will move forward */
                                            if (it.pixelColor == null) {
                                                horizontalPointer += NFireworks.PATTERN_FRAME_POINTER_INCREMENT_DECREMENT
                                                horizontalPointer = String.format("%.1f", horizontalPointer).toDouble()
                                                return@forEach
                                            }

                                            val pixelDust = DustParticleOptions(it.pixelPos, 0.9F)

                                            /* Render the pixel */
                                            ServerGamePacketManager.getMcWorld(explodePos.world!!).sendParticles(
                                                null, pixelDust, explodePos.x, verticalPointer,
                                                horizontalPointer, 1, 0.0, 0.0, 0.0, 1.0, true
                                            )

                                            horizontalPointer += NFireworks.PATTERN_FRAME_POINTER_INCREMENT_DECREMENT
                                            horizontalPointer = String.format("%.1f", horizontalPointer).toDouble()
                                        }
                                }

                                /*
                                * (Z+) H.Start <--> H.End (-Z)
                                *
                                *         ^ Facing X- ^
                                *
                                * */
                                (fireworkPatternFacingModX == -1) -> {
                                    val horizontalStart = String.format("%.1f", explodePos.z + (NFireworks.PATTERN_FRAME_INGAME_SIZE / 2)).toDouble()
                                    val horizontalEnd = String.format("%.1f", explodePos.z - (NFireworks.PATTERN_FRAME_INGAME_SIZE / 2)).toDouble()

                                    /* Pointer for horizontal pixel where used to locate the current position within the horizontal pixels */
                                    var horizontalPointer: Double = String.format("%.1f", horizontalStart).toDouble()

                                    horizontalPixels.stream()
                                        .sorted { hPixel1, hPixel2 -> hPixel1.horizontalIndex.compareTo(hPixel2.horizontalIndex) }
                                        .toList()
                                        .forEach {
                                            if (horizontalPointer <= horizontalEnd) horizontalPointer = horizontalStart

                                            /* Check if the pixelColor is null, if it is, horizontal pointer will move forward */
                                            if (it.pixelColor == null) {
                                                horizontalPointer -= NFireworks.PATTERN_FRAME_POINTER_INCREMENT_DECREMENT
                                                horizontalPointer = String.format("%.1f", horizontalPointer).toDouble()
                                                return@forEach
                                            }

                                            val pixelDust = DustParticleOptions(it.pixelPos, 0.9F)

                                            /* Render the pixel */
                                            ServerGamePacketManager.getMcWorld(explodePos.world!!).sendParticles(
                                                null, pixelDust, explodePos.x, verticalPointer,
                                                horizontalPointer, 1, 0.0, 0.0, 0.0, 1.0, true
                                            )

                                            horizontalPointer -= NFireworks.PATTERN_FRAME_POINTER_INCREMENT_DECREMENT
                                            horizontalPointer = String.format("%.1f", horizontalPointer).toDouble()
                                        }

                                }
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


