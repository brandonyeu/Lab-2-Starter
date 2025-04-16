/**
 * Welcome to this shiny step counter app -_-
 */

package edu.wpi.cs.cs4518.stepcounter_starter

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import edu.wpi.cs.cs4518.stepcounter_starter.databinding.ActivityMainBinding
//import java.io.FileWriter
//import java.io.IOException

class MainActivity : AppCompatActivity(), SensorEventListener {

	private lateinit var binding: ActivityMainBinding
	private val viewModel: CounterViewModel by viewModels()

	private lateinit var sensorManager: SensorManager
	private var linearAccelerometer: Sensor? = null
	private var isSensorActive = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
		linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

		viewModel.stepCount.observe(this, Observer { count ->
			binding.textViewCounter.text = count.toString()
		})

		binding.buttonStart.setOnClickListener {
			if (!isSensorActive) {
				linearAccelerometer?.let {
					sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
					isSensorActive = true
					Log.d(TAG, "Sensor registered")
				} ?: Log.e(TAG, "Linear Accelerometer not available")
			}
		}

		binding.buttonStop.setOnClickListener {
			if (isSensorActive) {
				sensorManager.unregisterListener(this)
				isSensorActive = false
				Log.d(TAG, "Sensor unregistered")
			}
		}

		binding.buttonReset.setOnClickListener {
			viewModel.resetSteps()
			Log.d(TAG, "Steps reset")
		}
	}

	override fun onSensorChanged(event: SensorEvent?) {
		event?.let {
			viewModel.addSensorData(it.values[0], it.values[1], it.values[2])
		}
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

	companion object {
		private const val TAG = "MainActivity"
	}
}
