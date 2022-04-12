package kz.kuanysh.pomodorosample

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    companion object {
        private const val SECOND = 1000L
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state

    var workTime: Long = 0
        set(value) {
            field = value * 1000
        }
    var breakTime: Long = 0
        set(value) {
            field = value * 1000
        }

    private var timer: CountDownTimer? = null

    fun startTimer(timerType: State.TimerType) {
        if (workTime == 0L || breakTime == 0L) return
        stopTimer()
        timer = when (timerType) {
            State.TimerType.WORK -> workTime
            State.TimerType.BREAK -> breakTime
        }.getTimer(
            onTick = { timer ->
                _state.value = State(timer, timerType, false)
            },
            onFinish = {
                _state.value = State(0, timerType, true)
            }
        ).start()
    }

    fun stopTimer() {
        timer?.cancel()
    }

    private fun Long.getTimer(onTick: (Long) -> Unit, onFinish: () -> Unit) =
        object : CountDownTimer(this, SECOND) {
            override fun onTick(time: Long) = onTick(time)

            override fun onFinish() = onFinish()
        }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

}

data class State(
    val timer: Long,
    val timerType: TimerType,
    val finished: Boolean,
) {
    enum class TimerType {
        WORK, BREAK;
    }
}