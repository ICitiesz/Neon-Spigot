package com.islandstudio.neon.shared.experimental.utils.coroutines

import com.islandstudio.neon.shared.core.exception.NeonException
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CloseableCoroutineScope(dispatcher: CoroutineContext): CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext = dispatcher + job

    fun launchJob(throwsError: Boolean = true, block: suspend CoroutineScope.() -> Unit): Job {
        return launch {
            try {
                block()
            } catch (e: Throwable) {
                /* Cancel job upon errors */
                job.cancel("Error while trying to execute job.", e)

                if (throwsError) throw NeonException("Error while trying to execute job.", e)
            } finally {
                /* Close job upon completion to avoid leaks */
                if (job.children.none { it.isActive }) job.cancel()
            }
        }
    }

    fun launchAsCompletableDeferred(throwsError: Boolean = true, block: suspend CoroutineScope.() -> Unit): CompletableDeferred<Unit> {
        val jobCompletion = CompletableDeferred<Unit>()

        launch {
            try {
                block()
                jobCompletion.complete(Unit)
            } catch (e: Throwable) {
                /* Cancel job upon errors */
                job.cancel("Error while trying to execute job.", e)

                if (throwsError) {
                    jobCompletion.completeExceptionally(NeonException("Error while trying to execute job.", e))
                }
            } finally {
                /* Close job upon completion to avoid leaks */
                if (job.children.none { it.isActive }) {
                    jobCompletion.complete(Unit)
                    job.cancel()
                }
            }
        }

        return jobCompletion
    }

    fun <T>launchAsCompletableDeferredResult(throwsError: Boolean = true, block: suspend CoroutineScope.() -> T): CompletableDeferred<T> {
        val jobCompletion = CompletableDeferred<T>()

        launch {
            try {
                val result = block()
                jobCompletion.complete(result)
            } catch (e: Throwable) {
                /* Cancel job upon errors */
                job.cancel("Error while trying to execute job.", e)

                if (throwsError) {
                    jobCompletion.completeExceptionally(NeonException("Error while trying to execute job.", e))
                }
            } finally {
                /* Close job upon completion to avoid leaks */
                if (job.children.none { it.isActive }) {
                    job.cancel()
                }
            }
        }

        return jobCompletion
    }
}