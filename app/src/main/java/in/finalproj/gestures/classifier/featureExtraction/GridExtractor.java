/*
 * GridExtractor.java
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
package in.finalproj.gestures.classifier.featureExtraction;

import in.finalproj.gestures.Gesture;

import java.util.ArrayList;


public class GridExtractor implements IFeatureExtractorConstCount {
	final static int SAMPLE_STEPS = 32;

	public Gesture sampleSignal(Gesture signal) {

		ArrayList<float[]> sampledValues = new ArrayList<float[]>();
		Gesture sampledSignal = new Gesture(sampledValues, signal.getLabel());
		float findex;

		for (int j = 0; j < SAMPLE_STEPS; ++j) {
			sampledValues.add(new float[3]);
			for (int i = 0; i < 3; ++i) {
				findex = (float) (signal.length() - 1) * j / (SAMPLE_STEPS - 1);
				float res = findex - (int) findex;
				sampledSignal.setValue(j, i, (1 - res) * signal.getValue((int) findex, i) + ((int) findex + 1 < signal.length() - 1 ? res * signal.getValue((int) findex + 1, i) : 0));
			}
		}

		return sampledSignal;
	}
}
