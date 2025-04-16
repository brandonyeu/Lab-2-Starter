package edu.wpi.cs.cs4518.stepcounter_starter

import android.util.Log
import kotlin.math.sqrt

object StepCounterAlgorithm {
	private const val TAG = "StepCounterAlgorithm"

	//TODO: you will implement the *local* step detection algorithm here
	// Remember that you are recording (x,y,z) data, but the detection should be based on the magnitude
	// Also you should check the documentation of the jdsp as mentioned in the project description
	// currently it just returns some random number, which is useful to testing the overall workflow
	fun detectSteps(sensorData: List<FloatArray>): Int {
		val magnitudes = sensorData.map { (x, y, z) -> sqrt(x * x + y * y + z * z) }

		var steps = 0
		for (i in 1 until magnitudes.size - 1) {
			if (magnitudes[i] > 1.2 && magnitudes[i] > magnitudes[i - 1] && magnitudes[i] > magnitudes[i + 1]) {
				steps++
			}
		}

		Log.d(TAG, "Detected $steps steps")
		return steps
	}
}
