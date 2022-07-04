package com.bonita.logcatsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bonita.logcatsample.databinding.ActivityMainBinding

/**
 * Logcat Activity
 *
 * @author bonita
 * @date 2022-06-29
 */
class MainActivity : AppCompatActivity() {

    // View binding
    private lateinit var viewBinding: ActivityMainBinding

    private var logcatProcessor: LogcatProcessor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        bindButton()
    }

    override fun onDestroy() {
        logcatProcessor?.let {
            it.isRecording = false
            it.interrupt()
        }

        super.onDestroy()
    }

    /**
     * Button 설정
     */
    private fun bindButton() {
        viewBinding.startButton.setOnClickListener {
            logcatProcessor
                ?.takeIf { it.isRecording }
                ?: run {
                    // 로그 기록 시작
                    logcatProcessor = LogcatProcessor()
                    logcatProcessor!!.start()
                }
        }


        viewBinding.endButton.setOnClickListener {
            logcatProcessor
                ?.takeIf { it.isRecording }
                ?.run {
                    // 로그 기록 중지
                    isRecording = false
                    logcatProcessor = null
                }

        }
    }
}