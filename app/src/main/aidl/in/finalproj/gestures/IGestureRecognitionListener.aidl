
package in.finalproj.gestures;
import in.finalproj.gestures.classifier.Distribution;

interface IGestureRecognitionListener {
	void onGestureRecognized(in Distribution distribution);

	 void onGestureLearned(String gestureName);

	 void onTrainingSetDeleted(String trainingSet);
} 


