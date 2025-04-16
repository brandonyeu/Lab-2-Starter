Christian Reynolds (cjreynolds1@wpi.edu) 
Brandon Yeu (byeu@wpi.edu)

The app has 4 basic elements on the screen, a textview that displays the current steps recorded, a button to start recording,
a button to stop recording, and a button to reset the step counts to 0. 

We chose to use a mobile-only architecture for a few reasons. For design purposes, mobile-only architecture has some benefits such as 
offline capabilities, minimizing latency, and simplifying setup and maintainence. Also, we felt more comfortable with Java/Kotlin as opposed to Flask. 

Our app collects data, uses the magnitude, we reduce noise and read the peaks of the data to count the actual steps. The step counting is persistent, 
so when the screen is rotated, the step count remains the same, instead of resetting. 