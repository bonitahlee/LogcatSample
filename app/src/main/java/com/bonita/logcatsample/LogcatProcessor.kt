package com.bonita.logcatsample

import android.os.Environment
import android.text.TextUtils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 로그 기록 Thread
 *
 * @author bonita
 * @date 2022-06-29
 */
class LogcatProcessor: Thread() {

    // 로그 기록중인지 확인
    var isRecording = true

    // log buffer 크기를 늘려야 좀더 많은 로그를 볼 수 있음
    private val logcatBufferCmd = arrayOf("logcat", "-G", "1M")
    private val logcatExecuteCmd = arrayOf("logcat", "-v", "time")

    private val logDirName: String = Environment.getExternalStorageDirectory().path + File.separator + "LogData"
    private val logFileName = "log_" + getDateEN() + ".log"

    private var logProcess: Process? = null

    private var outputStream: FileOutputStream?

    init {
        // 로그 저장할 폴더 생성
        File(logDirName).mkdirs()

        outputStream = try {
            FileOutputStream(File(logDirName, logFileName))
        } catch (e: FileNotFoundException) {
            null
        }
    }

    override fun run() {
        isRecording = true

        var bufferReader: BufferedReader? = null

        try {
            Runtime.getRuntime().exec(logcatBufferCmd)
            logProcess = Runtime.getRuntime().exec(logcatExecuteCmd)

            // write to file
            var dataLength: Long = 0

            bufferReader = BufferedReader(InputStreamReader(logProcess?.inputStream), 1024)
            bufferReader.use { reader ->
                var line: String? = ""
                while (isRecording && reader.readLine().also { line = it } != null) {
                    if (TextUtils.isEmpty(line)) {
                        continue
                    }

                    outputStream?.run {
                        val data = getDateEN() + "  " + line + "\n"
                        write(data.toByteArray())
                        dataLength += data.toByteArray().size.toLong()
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            isRecording = false

            bufferReader?.close()

            logProcess?.destroy()
            logProcess = null

            try {
                outputStream?.close()
                outputStream = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getDateEN(): String {
        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date1 = format1.format(Date(System.currentTimeMillis()))
        return date1.replace(":", "_")
    }
}