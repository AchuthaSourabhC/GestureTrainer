/*
 * GestureRecorder.java
 */
package in.finalproj.gestures.recorder;

import in.finalproj.gestures.classifier.featureExtraction.LowPassFilter;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class GestureRecorder implements SensorEventListener {

	public enum RecordMode {
		MOTION_DETECTION, PUSH_TO_GESTURE
	};

	final int MIN_GESTURE_SIZE = 12;
	float THRESHOLD = 3.0f;
	SensorManager sensorManager;
	boolean isRecording;

	int stepsSinceNoMovement;
	ArrayList<float[]> gestureValues;
	Context context;
	GestureRecorderListener listener;
	boolean isRunning;
	RecordMode recordMode = RecordMode.MOTION_DETECTION;
	

	public GestureRecorder(Context context) {
		this.context = context;
		
	}

	private float calcVectorNorm(float[] values) {
		float norm = (float) Math.sqrt(values[SensorManager.DATA_X] * values[SensorManager.DATA_X] + values[SensorManager.DATA_Y] * values[SensorManager.DATA_Y] + values[SensorManager.DATA_Z]
				* values[SensorManager.DATA_Z]) - 9.9f;
		return norm;
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

	public RecordMode getRecordMode() {
		return recordMode;
	}

	public void setThreshold(float threshold) {
		THRESHOLD = threshold;
		System.err.println("New Threshold " + threshold);
	}

	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// do nothing
	}

	public void onPushToGesture(boolean pushed) {

		if (recordMode == RecordMode.PUSH_TO_GESTURE) {
			isRecording = pushed;
			if (isRecording) {
				gestureValues = new ArrayList<float[]>();
			} else {
				if (gestureValues.size() > MIN_GESTURE_SIZE) {
					listener.onGestureRecorded(gestureValues);
				}
				gestureValues = null;
			}
		}
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {

		float[] value = { sensorEvent.values[SensorManager.DATA_X], sensorEvent.values[SensorManager.DATA_Y], sensorEvent.values[SensorManager.DATA_Z] };
		//float[] value = filter.signalFilter(sensorValue);
		switch (recordMode) {
		case MOTION_DETECTION:
			if (isRecording) {
				gestureValues.add(value);
				if (calcVectorNorm(value) < THRESHOLD) {
					stepsSinceNoMovement++;
				} else {
					stepsSinceNoMovement = 0;
				}
			} else if (calcVectorNorm(value) >= THRESHOLD) {
				isRecording = true;
				stepsSinceNoMovement = 0;
				gestureValues = new ArrayList<float[]>();
				gestureValues.add(value);
			}
			if (stepsSinceNoMovement == 15) {

				//System.out.println("Length is: " + String.valueOf(gestureValues.size() - 10));
				if (gestureValues.size() - 10 > MIN_GESTURE_SIZE) {
					listener.onGestureRecorded(gestureValues.subList(0, gestureValues.size() - 10));
				}
				gestureValues = null;
				stepsSinceNoMovement = 0;
				isRecording = false;
			}
			break;
		case PUSH_TO_GESTURE:
			if (isRecording) {
				gestureValues.add(value);
			}
			break;
		}

	}

	public void registerListener(GestureRecorderListener listener) {
		this.listener = listener;
		start();
	}

	public void setRecordMode(RecordMode recordMode) {
		this.recordMode = recordMode;
	}

	public void start() {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
		isRunning = true;
	}

	public void stop() {
		sensorManager.unregisterListener(this);
		isRunning = false;
	}

	public void unregisterListener(GestureRecorderListener listener) {
		this.listener = null;
		stop();
	}

	public void pause(boolean b) {
		if (b) {
			sensorManager.unregisterListener(this);
		} else {
			sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
		}
	}

}