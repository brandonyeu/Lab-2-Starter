/*
 * Project Name: My Accelerometer Data Recorder
 * Author: Tian Guo
 * Created: November 14, 2024
 * Description: This simple app records accelerometer data from an Android device
 *              and saves it to a CSV file for offline analysis.
 *
 * Note:
 * 1. screen rotation will stop the recording, and flush the remaining writes to the file system.
 * So you will need to turn off the auto rotation in Android
 * 2. the accelerometer data can be downloaded from the Device Explorer under /sdcard/Download
 * Version: 1.0
 */


package edu.wpi.cs.cs4518.saveaccelerometerdata


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), SensorEventListener {
	private var sensorManager: SensorManager? = null
	private var accelerometer: Sensor? = null
	private var fileWriter: FileWriter? = null
	private var accelerometerRaw: Sensor? = null
	private var fileWriterRaw: FileWriter? = null
	private lateinit var statusTextView: TextView
	private lateinit var startButton: Button
	private lateinit var stopButton: Button
	private var isRecording = false

	private val desiredFrequencyHz = 50
	private val samplingPeriodUs = 1_000_000 / desiredFrequencyHz

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		statusTextView = findViewById(R.id.statusTextView)
		startButton = findViewById(R.id.startButton)
		stopButton = findViewById(R.id.stopButton)

		stopButton.isEnabled = false

		startButton.setOnClickListener {
			initialize()
		}

		stopButton.setOnClickListener {
			stopRecording()
		}
	}

	private fun initialize() {
		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		// accelerometer = sensorManager?.getDefaultSensor(SENSOR_TYPE)
		accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
		accelerometerRaw = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

		accelerometer?.let {
			// note that the sampling rate is just a hint; so we might not get 50Hz exact.
			sensorManager?.registerListener(this, it, samplingPeriodUs)
		}
		accelerometerRaw?.let {
			sensorManager?.registerListener(this, it, samplingPeriodUs)
		}


		try {
			val linearFile = createCSVFile("LinearAccel")
			val rawFile = createCSVFile("Accel")

			fileWriter = FileWriter(linearFile)
			fileWriterRaw = FileWriter(rawFile)

			fileWriter?.append("Timestamp,X,Y,Z\n")
			fileWriterRaw?.append("Timestamp,X,Y,Z\n")

			isRecording = true
			statusTextView.text = "Recording..."
			startButton.isEnabled = false
			stopButton.isEnabled = true
		} catch (e: IOException) {
			e.printStackTrace()
			Toast.makeText(this, "Failed to create files", Toast.LENGTH_SHORT).show()
		}
	}


	private fun stopRecording() {
		if (isRecording) {
			sensorManager?.unregisterListener(this)
			try {
				fileWriter?.close()
				fileWriterRaw?.close()
				Toast.makeText(this, "Recordings saved", Toast.LENGTH_SHORT).show()
			} catch (e: IOException) {
				e.printStackTrace()
				Toast.makeText(this, "Failed to save files", Toast.LENGTH_SHORT).show()
			}
			isRecording = false
			statusTextView.text = "Recording stopped"
			startButton.isEnabled = true
			stopButton.isEnabled = false
		}
	}



	@Throws(IOException::class)
	private fun createCSVFile(prefix: String): File {
		val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
		val fileName = "${prefix}Data_$timestamp.csv"
		val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
		if (!storageDir.exists()) {
			storageDir.mkdirs()
		}
		val file = File(storageDir, fileName)
		Log.d("MainActivity", "File saved at: ${file.absolutePath}")
		return file
	}


	override fun onSensorChanged(event: SensorEvent) {
		if (!isRecording) return

		val currentTimestamp = System.currentTimeMillis()
		val x = event.values[0]
		val y = event.values[1]
		val z = event.values[2]

		try {
			when (event.sensor.type) {
				Sensor.TYPE_LINEAR_ACCELERATION -> {
					fileWriter?.append(
						String.format(Locale.getDefault(), "%d,%.3f,%.3f,%.3f\n", currentTimestamp, x, y, z)
					)
				}
				Sensor.TYPE_ACCELEROMETER -> {
					fileWriterRaw?.append(
						String.format(Locale.getDefault(), "%d,%.3f,%.3f,%.3f\n", currentTimestamp, x, y, z)
					)
				}
			}
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}


	override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
		// Not used
	}

	override fun onDestroy() {
		super.onDestroy()
		stopRecording()
	}

	companion object {
		const val SENSOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION
	}
}
