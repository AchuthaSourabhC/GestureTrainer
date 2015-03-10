/*
 * NormExtractor.java
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


public class NormExtractor implements IFeatureExtractor {

	public Gesture sampleSignal(Gesture signal) {
		/*ArrayList<float[]> sampledValues = new ArrayList<float[]>(signal.length());
		Gesture sampledSignal = new Gesture(sampledValues, signal.getLabel());

		float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
		for (int i = 0; i < signal.length(); i++) {
			for (int j = 0; j < 3; j++) {
				if (signal.getValue(i, j) > max) {
					max = signal.getValue(i, j);
				}
				if (signal.getValue(i, j) < min) {
					min = signal.getValue(i, j);
				}
			}
		}
		for (int i = 0; i < signal.length(); ++i) {
			sampledValues.add(new float[3]);
			for (int j = 0; j < 3; ++j) {
				sampledSignal.setValue(i, j, (signal.getValue(i, j) - min) / (max - min));
			}
		}
		return sampledSignal;*/
		return signal;
	}
}
