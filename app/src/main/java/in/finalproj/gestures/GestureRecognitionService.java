/*
 * GestureRecognitionService.java
 */
package in.finalproj.gestures;

import in.finalproj.gestures.classifier.Distribution;
import in.finalproj.gestures.classifier.GestureClassifier;
import in.finalproj.gestures.classifier.featureExtraction.NormedGridExtractor;
import in.finalproj.gestures.recorder.GestureRecorder;
import in.finalproj.gestures.recorder.GestureRecorderListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;


public class GestureRecognitionService extends Service implements GestureRecorderListener {

	GestureRecorder recorder;
	GestureClassifier classifier;
	String activeTrainingSet;
	String activeLearnLabel;
	boolean isLearning, isClassifying;

	Set<IGestureRecognitionListener> listeners = new HashSet<IGestureRecognitionListener>();

	IBinder gestureRecognitionServiceStub = new IGestureRecognitionService.Stub() {

		@Override
		public void deleteTrainingSet(String trainingSetName) throws RemoteException {
			if (classifier.deleteTrainingSet(trainingSetName)) {
				for (IGestureRecognitionListener listener : listeners) {
					try {
						listener.onTrainingSetDeleted(trainingSetName);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onPushToGesture(boolean pushed) throws RemoteException {
			recorder.onPushToGesture(pushed);
		}

		@Override
		public void registerListener(IGestureRecognitionListener listener) throws RemoteException {
			if (listener != null) {
				listeners.add(listener);
			}
		}

		@Override
		public void startClassificationMode(String trainingSetName) throws RemoteException {
			activeTrainingSet = trainingSetName;
			isClassifying = true;
			recorder.start();
			classifier.loadTrainingSet(trainingSetName);
		}

		@Override
		public void startLearnMode(String trainingSetName, String gestureName) throws RemoteException {
			activeTrainingSet = trainingSetName;
			activeLearnLabel = gestureName;
			isLearning = true;
			// recorder.setRecordMode(GestureRecorder.RecordMode.PUSH_TO_GESTURE);
		}

		@Override
		public void stopLearnMode() throws RemoteException {
			isLearning = false;

			// recorder.setRecordMode(GestureRecorder.RecordMode.MOTION_DETECTION);
		}

		@Override
		public void unregisterListener(IGestureRecognitionListener listener) throws RemoteException {
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				stopClassificationMode();
			}
		}

		@Override
		public List<String> getGestureList(String trainingSet) throws RemoteException {
			return classifier.getLabels(trainingSet);
		}

		@Override
		public void stopClassificationMode() throws RemoteException {
			isClassifying = false;
			recorder.stop();

		}

		@Override
		public void deleteGesture(String trainingSetName, String gestureName) throws RemoteException {
			classifier.deleteLabel(trainingSetName, gestureName);
			classifier.commitData();
		}

		@Override
		public boolean isLearning() throws RemoteException {
			return isLearning;
		}

		@Override
		public void setThreshold(float threshold) throws RemoteException {
			recorder.setThreshold(threshold);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		recorder.registerListener(this);
		return gestureRecognitionServiceStub;
	}

	@Override
	public void onCreate() {
		recorder = new GestureRecorder(this);
		classifier = new GestureClassifier(new NormedGridExtractor(), this);
		super.onCreate();
	}

	@Override
	public void onGestureRecorded(List<float[]> values) {
		if (isLearning) {

			classifier.trainData(activeTrainingSet, new Gesture(values, activeLearnLabel));
			classifier.commitData();
			for (IGestureRecognitionListener listener : listeners) {
				try {
					listener.onGestureLearned(activeLearnLabel);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Trained");
		} else if (isClassifying) {
			recorder.pause(true);
			Distribution distribution = classifier.classifySignal(activeTrainingSet, new Gesture(values, null));
			recorder.pause(false);
			if (distribution != null && distribution.size() > 0) {
				for (IGestureRecognitionListener listener : listeners) {
					try {
						listener.onGestureRecognized(distribution);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		recorder.unregisterListener(this);
		return super.onUnbind(intent);
	}
	
	public GestureClassifier getClassifier(){
		return classifier;
	}

}
