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
        // タイマー作動
        if (onTimer.get() == true) return
        onTimer.set(true)
        countTimer()
    }

    fun stopTimer() {
        // タイマー停止
        if (onTimer.get() != true) return
        job.cancel()
        onTimer.set(false)
    }

    fun resetTimer() {
        seconds.set(0)
        stopTimer()
    }

    fun pauseTimer() {
        // タイマー動作中だったら一時停止する
        if (onTimer.get() == true) {
            resumeTimer.set(true)
            stopTimer()
        }
    }

    fun resumeTimer() {
        // バックグラウンドから戻ったらタイマーを再開する
        if (resumeTimer.get() == true) {
            resumeTimer.set(false)
            startTimer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancelChildren()
    }
}