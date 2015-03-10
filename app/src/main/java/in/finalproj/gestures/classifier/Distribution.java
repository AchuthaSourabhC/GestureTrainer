/*
 * Distribution.java
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

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Distribution implements Parcelable {
	private final Map<String, Double> distribution;
	String best;
	double minDistance = Double.MAX_VALUE;

	public Distribution() {
		distribution = new HashMap<String, Double>();
	}

	private Distribution(Parcel in) {

		distribution = new HashMap<String, Double>();
		Bundle bundle = in.readBundle();
		for (String key : bundle.keySet()) {
			distribution.put(key, bundle.getDouble(key));
		}
		best = in.readString();
		minDistance = in.readDouble();
	}

	public void addEntry(String tag, double distance) {
		if (!distribution.containsKey(tag) || distance < distribution.get(tag)) {
			distribution.put(tag, distance);
			if (distance < minDistance) {
				minDistance = distance;
				best = tag;
			}
		}
	}

	public String getBestMatch() {
		return best;
	}

	public double getBestDistance() {
		return minDistance;
	}

	public int size() {
		return distribution.size();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		Bundle bundle = new Bundle();
		for (String key : distribution.keySet()) {
			bundle.putDouble(key, distribution.get(key));
		}
		out.writeBundle(bundle);
		out.writeString(best);
		out.writeDouble(minDistance);
	}

	public static final Parcelable.Creator<Distribution> CREATOR = new Creator<Distribution>() {

		@Override
		public Distribution[] newArray(int size) {
			return new Distribution[size];
		}

		@Override
		public Distribution createFromParcel(Parcel in) {
			return new Distribution(in);
		}
	};
}
