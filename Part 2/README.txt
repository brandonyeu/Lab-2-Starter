Christian Reynolds (cjreynolds1@wpi.edu) 
Brandon Yeu (byeu@wpi.edu)

The app has 3 basic elements on the screen, a textview that displays whether the app is currently recording, a button to start recording,
and a button to stop recording. 

The way we implemented the required features of Part 2, there are separate variables for the raw accelerometer and the linear accelerometer. 
In initialize(), we define separate files for accelerometer and linear accelerometer, with separate file writers for each. 
The function stopRecording() stops recording for both. All of the functions for recording and writing to the CSV are parallel for both the 
accelerometer and the linear accelerometer. The function createCSVFile() now takes in a parameter for the prefix of the file, being 
LinearAccel and Accel for each of the types of sensor data. 