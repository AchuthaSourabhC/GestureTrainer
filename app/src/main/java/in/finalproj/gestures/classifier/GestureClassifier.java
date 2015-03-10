/*
 * GestureClassifier.java
 *
 * Created: 18.08.2011
 *
 * Copyright (C) 2011 Robert Nesselrath
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package in.finalproj.gestures.classifier;

import in.finalproj.gestures.Gesture;
import in.finalproj.gestures.classifier.featureExtraction.IFeatureExtractor;
import in.finalproj.gestures.classifier.featureExtraction.LowPassFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;

public class GestureClassifier {

	public List<Gesture> trainingSet = Collections.emptyList();
	protected IFeatureExtractor featureExtractor;
	protected String activeTrainingSet = "";
	private final Context context;
	private LowPassFilter filter;

	public GestureClassifier(IFeatureExtractor fE, Context context) {
		trainingSet = new ArrayList<Gesture>();
		featureExtractor = fE;
		filter = new LowPassFilter();
		this.context = context;
	}

	public boolean commitData() {
		if (activeTrainingSet != null && activeTrainingSet != "") {
			try {
				FileOutputStream fos = new FileOutputStream(new File(context.getExternalFilesDir(null), activeTrainingSet + ".gst").toString());
				ObjectOutputStream o = new ObjectOutputStream(fos);
				o.writeObject(trainingSet);
				o.close();
				fos.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean trainData(String trainingSetName, Gesture signal) {
		loadTrainingSet(trainingSetName);
		trainingSet.add(featureExtractor.sampleSignal(signal));
		//trainingSet.add(signal);
		return true;
	}

	@SuppressWarnings("unchecked")
	public void loadTrainingSet(String trainingSetName) {
		if (!trainingSetName.equals(activeTrainingSet)) {
			activeTrainingSet = trainingSetName;
			FileInputStream input;
			ObjectInputStream o;
			try {
				input = new FileInputStream(new File(context.getExternalFilesDir(null), activeTrainingSet + ".gst"));
				o = new ObjectInputStream(input);
				trainingSet = (ArrayList<Gesture>) o.readObject();
				try {
					o.close();
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				trainingSet = new ArrayList<Gesture>();
			}
		}
	}

	public boolean checkForLabel(String trainingSetName, String label) {
		loadTrainingSet(trainingSetName);
		for (Gesture s : trainingSet) {
			if (s.getLabel().equals(label)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkForTrainingSet(String trainingSetName) {
		File file = new File(trainingSetName + ".gst");
		return file.exists();
	}

	public boolean deleteTrainingSet(String trainingSetName) {
		System.out.printf("Try to delete training set %s\n", trainingSetName);
		if (activeTrainingSet != null && activeTrainingSet.equals(trainingSetName)) {
			trainingSet = new ArrayList<Gesture>();
		}
		// File file = new File("\\sdcard\\" + activeTrainingSet + ".gst");
		// if (file.exists()) {
		// file.delete();
		// return true;
		// }
		// return false;
		return context.deleteFile(activeTrainingSet + ".gst");

	}

	public boolean deleteLabel(String trainingSetName, String label) {
		loadTrainingSet(trainingSetName);
		boolean labelExisted = false;
		ListIterator<Gesture> it = trainingSet.listIterator();
		while (it.hasNext()) {
			Gesture s = it.next();
			if (s.getLabel().equals(label)) {
				it.remove();
				labelExisted = true;
			}
		}
		return labelExisted;
	}

	public List<String> getLabels(String trainingSetName) {
		loadTrainingSet(trainingSetName);
		List<String> labels = new ArrayList<String>();

		for (Gesture s : trainingSet) {
			if (!labels.contains(s.getLabel())) {
				labels.add(s.getLabel());
			}
		}
		return labels;
	}

	public IFeatureExtractor getFeatureExtractor() {
		return featureExtractor;
	}

	public void setFeatureExtractor(IFeatureExtractor featureExtractor) {
		this.featureExtractor = featureExtractor;
	}

	public Distribution classifySignal(String trainingSetName, Gesture signal) {
		if (trainingSetName == null) {
			System.err.println("No Training Set Name specified");
			trainingSetName = "default";
		}
		if (!trainingSetName.equals(activeTrainingSet)) {
			loadTrainingSet(trainingSetName);
		}

		Distribution distribution = new Distribution();
		Gesture sampledSignal = featureExtractor.sampleSignal(signal);
		//Gesture sampledSignal = signal;
		for (Gesture s : trainingSet) {
			double dist = DTWAlgorithm.calcDistance(s, sampledSignal);
			distribution.addEntry(s.getLabel(), dist);
		}
		if (trainingSet.isEmpty()) {
			System.err.printf("No training data for trainingSet %s available.\n", trainingSetName);
		}

		return distribution;
	}

}