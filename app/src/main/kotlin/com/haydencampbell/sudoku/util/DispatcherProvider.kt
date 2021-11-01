package com.haydencampbell.sudoku.util

import kotlin.coroutines.CoroutineContext

interface DispatcherProvider {
    fun provideUIContext(): CoroutineContext
    fun provideIOContext(): CoroutineContext
}

