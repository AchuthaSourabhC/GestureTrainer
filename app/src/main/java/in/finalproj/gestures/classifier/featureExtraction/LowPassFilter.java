package in.finalproj.gestures.classifier.featureExtraction;

import java.util.ArrayList;

import in.finalproj.gestures.Gesture;

public class LowPassFilter {
	
	// Constants for the low-pass filters
	private float timeConstant = 0.15f;
	private float alpha = 0.2f;
	private float dt = 0;
	 
	// Timestamps for the low-pass filters
	private float timestamp = System.nanoTime();
	private float timestampOld = System.nanoTime();
	 
	// Gravity and linear accelerations components for the
	// Wikipedia low-pass filter
	private float[] gravity = new float[]
	{ 0, 0, 0 };
	 
	private float[] linearAcceleration = new float[]
	{ 0, 0, 0 };
	 
	// Raw accelerometer data
	private float[] input = new float[]
	{ 0, 0, 0 };
	 
	private int count = 0;
	
	public LowPassFilter(){
		
	}
	 
	/**
	* Add a sample.
	*
	* @param acceleration
	*            The acceleration data.
	* @return Returns the output of the filter.
	*/
	public float[] signalFilter(float[] acceleration)
	{
	// Get a local copy of the sensor values
	System.arraycopy(acceleration, 0, this.input, 0, acceleration.length);
	 
	timestamp = System.nanoTime();
	 
	// Find the sample period (between updates).
	// Convert from nanoseconds to seconds
	dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));
	 
	count++;
	         
	alpha = timeConstant / (timeConstant + dt);
	 
	gravity[0] = alpha * gravity[0] + (1 - alpha) * input[0];
	gravity[1] = alpha * gravity[1] + (1 - alpha) * input[1];
	gravity[2] = alpha * gravity[2] + (1 - alpha) * input[2];
	 
	linearAcceleration[0] = input[0] - gravity[0];
	linearAcceleration[1] = input[1] - gravity[1];
	linearAcceleration[2] = input[2] - gravity[2];
	 
	return linearAcceleration;
	}
	
	public Gesture sampleSignal(Gesture signal){
		
		ArrayList<float[]> sampledValues = new ArrayList<float[]>(signal.length());
		Gesture sampledSignal = new Gesture(sampledValues, signal.getLabel());
		


		return sampledSignal;
		
	}

}
