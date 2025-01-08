package cn.com.zt.camera.until

import android.os.CountDownTimer

class RecordingTimer(
    var totalTimeInMillis: Long = 1000,
    var countDownInterval: Long = 1000,
    private var timerCallBack: TimerCallBack
) {
    private var timeLeftInMillis: Long = 1000
    private var isTimerRunning: Boolean = false
    private var countDownTimer: CountDownTimer? = null

    fun startTimer() {
        if (isTimerRunning) {
            return
        }
        isTimerRunning = true
        timeLeftInMillis = totalTimeInMillis
        countDownTimer = object : CountDownTimer(timeLeftInMillis, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                timerCallBack.onTick(millisUntilFinished)
            }

            override fun onFinish() {
                isTimerRunning = false
                timerCallBack.onFinish()
            }
        }.start()
    }

    private fun releaseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
    }

    fun stopTimer() {
        releaseTimer()
        timeLeftInMillis = totalTimeInMillis
    }
}

interface TimerCallBack {
    fun onTick(millisUntilFinished: Long)
    fun onFinish()
}