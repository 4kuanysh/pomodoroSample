package kz.kuanysh.pomodorosample

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.state.observe(this) { state ->
            val seconds = state.timer / 1000
            findViewById<TextView>(R.id.labelTimer).text = "${seconds / 60}: ${seconds % 60}"

            if (state.finished) {
                when (state.timerType) {
                    State.TimerType.WORK -> {
                        viewModel.startTimer(State.TimerType.BREAK)
                        showBreakMessage()
                    }
                    State.TimerType.BREAK -> {
                        viewModel.startTimer(State.TimerType.WORK)
                    }
                }
            }
        }

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            viewModel.startTimer(State.TimerType.WORK)
        }

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            viewModel.stopTimer()
        }

        findViewById<EditText>(R.id.inputWorkTime).doOnTextChanged { text, _, _, _ ->
            viewModel.workTime =
                text.toString().takeIf { it.isDigitsOnly() && it.isNotBlank() }?.toLong() ?: 0
        }

        findViewById<EditText>(R.id.inputBreakTime).doOnTextChanged { text, _, _, _ ->
            viewModel.breakTime =
                text.toString().takeIf { it.isDigitsOnly() && it.isNotBlank() }?.toLong() ?: 0
        }
    }

    private fun showBreakMessage() {
        Toast.makeText(
            this,
            "Break time for ${viewModel.breakTime / 60000} minutes ${viewModel.breakTime % 60000} seconds",
            Toast.LENGTH_SHORT
        ).show()
    }
}