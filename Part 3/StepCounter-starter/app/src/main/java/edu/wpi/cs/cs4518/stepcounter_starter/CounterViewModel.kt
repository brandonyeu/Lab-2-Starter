package edu.wpi.cs.cs4518.stepcounter_starter

// import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

class CounterViewModel : ViewModel() {

	private val _stepCount = MutableLiveData(0)
	val stepCount: LiveData<Int> = _stepCount

	private val sensorDataQueue = ConcurrentLinkedQueue<FloatArray>()

	// TODO: you will need to implement this function so that
	// 1. each sensor data will be saved in a thread-safe data structure
	// 2. once we have accumulated 50 sensor data samples, call the processSensorData
	fun addSensorData(x: Float, y: Float, z: Float) {
		sensorDataQueue.add(floatArrayOf(x, y, z))

		if (sensorDataQueue.size >= 50) {
			val batch = mutableListOf<FloatArray>()
			repeat(50) {
				sensorDataQueue.poll()?.let { batch.add(it) }
			}
			processSensorData(batch)
		}
	}
	// TODO:
	//  1. pull the sensor data samples and then pass them to the step detection algorithm
	//  2. because the algorithm might be compute or I/O intensive,
	//  you will should run the algorithm in a coroutine.
	//      2.1: if you want to do the mobile-only architecture, use StepCounterAlgorithm
	//      2.2: if you want to try the server-based architecture, then the networking code
	//          can also go here (i.e., send request and then parse response)
	//  3. Once you get the detected step counts, use it to update the current step
	private fun processSensorData(batch: List<FloatArray>) {
		viewModelScope.launch(Dispatchers.Default) {
			val newSteps = StepCounterAlgorithm.detectSteps(batch)
			_stepCount.postValue((_stepCount.value ?: 0) + newSteps)
		}
	}

	fun resetSteps() {
		_stepCount.value = 0
		sensorDataQueue.clear()
	}
}
