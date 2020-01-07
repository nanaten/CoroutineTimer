package com.nanaten.coroutinetimer

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainViewModel : ViewModel(), CoroutineScope {
    val onTimer = ObservableField<Boolean>()
    val resumeTimer = ObservableField<Boolean>()
    val seconds = ObservableField<Int>()
    lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Main

    fun countTimer() {
        job = launch {
            val timer = onTimer.get() ?: false
            while (timer) {
                delay(1000)
                val time = seconds.get() ?: 0
                seconds.set(time + 1)
            }
        }
    }

    fun startTimer() {
        val timer = onTimer.get() ?: false
        if (timer) return
        onTimer.set(true)
        countTimer()
    }

    fun stopTimer() {
        job.cancel()
        onTimer.set(false)
    }

    fun resetTimer() {
        seconds.set(0)
        stopTimer()
    }

    fun pauseTimer() {
        val timer = onTimer.get() ?: false
        if (timer) {
            resumeTimer.set(true)
            stopTimer()
        }
    }

    fun resumeTimer() {
        val timer = resumeTimer.get() ?: false
        if (timer) {
            resumeTimer.set(false)
            startTimer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancelChildren()
    }
}