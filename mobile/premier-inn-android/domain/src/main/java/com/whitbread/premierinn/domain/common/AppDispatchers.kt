package com.whitbread.premierinn.domain.common

import kotlinx.coroutines.CoroutineDispatcher

data class AppDispatchers(
    /**
     * Optimized for disk and network IO (e.g., Retrofit calls, Room database, File system).
     * This pool is elastic and can scale to many threads.
     */
    val io: CoroutineDispatcher,

    /**
     * Optimized for CPU-intensive work (e.g., sorting lists, JSON parsing, image processing).
     * The thread pool size is limited to the number of CPU cores.
     */
    val default: CoroutineDispatcher,

    /**
     * The Main/UI thread dispatcher. Use this for UI work and UI-facing state changes
     */
    val main: CoroutineDispatcher
)