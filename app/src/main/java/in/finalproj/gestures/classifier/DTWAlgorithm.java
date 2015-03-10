/*
 * DTWAlgorithm.java
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

import java.util.ArrayList;


public class DTWAlgorithm {

	static float OFFSET_PENALTY = .5f;

	static private float pnorm(ArrayList<Double> vector, int p) {
		float result = 0, sum;
		for (double b : vector) {
			sum = 1;
			for (int i = 0; i < p; ++i) {
				sum *= b;
			}
			result += sum;
		}
		return (float) Math.pow(result, 1.0 / p);
	}

	static public float calcDistance(Gesture a, Gesture b) {
		int signalDimensions = a.getValues().get(0).length;
		int signal1Length = a.length();
		int signal2Length = b.length();

		// initialize matrices
		float distMatrix[][];
		float costMatrix[][];

		distMatrix = new float[signal1Length][];
		costMatrix = new float[signal1Length][];

		for (int i = 0; i < signal1Length; ++i) {
			distMatrix[i] = new float[signal2Length];
			costMatrix[i] = new float[signal2Length];
		}

		ArrayList<Double> vec;

		// calculate distances
		for (int i = 0; i < signal1Length; ++i) {
			for (int j = 0; j < signal2Length; ++j) {
				vec = new ArrayList<Double>();
				for (int k = 0; k < signalDimensions; ++k) {
					vec.add((double) (a.getValue(i, k) - b.getValue(j, k)));
				}
				distMatrix[i][j] = pnorm(vec, 2);
			}
		}

		// genetischer Algorithmus um den günstigsten Pfad zu finden
		for (int i = 0; i < signal1Length; ++i) {
			costMatrix[i][0] = distMatrix[i][0];
		}

		for (int j = 1; j < signal2Length; ++j) {
			for (int i = 0; i < signal1Length; ++i) {
				if (i == 0) {
					costMatrix[i][j] = costMatrix[i][j - 1] + distMatrix[i][j];
				} else {
					float minCost, cost;
					// i-1, j-1
					minCost = costMatrix[i - 1][j - 1] + distMatrix[i][j];
					// i-1, j
					if ((cost = costMatrix[i - 1][j] + distMatrix[i][j]) < minCost) {
						minCost = cost + OFFSET_PENALTY;
					}
					// i, j-1
					if ((cost = costMatrix[i][j - 1] + distMatrix[i][j]) < minCost) {
						minCost = cost + OFFSET_PENALTY;
					}
					costMatrix[i][j] = minCost;
				}
			}
		}
		return costMatrix[signal1Length - 1][signal2Length - 1];
	}

}
