/*
 * Gesture.java
 */
package in.finalproj.gestures;

import java.io.Serializable;
import java.util.List;

public class Gesture implements Serializable {

	private String label;
	private List<float[]> values;

	public Gesture(List<float[]> values, String label) {
		setValues(values);
		setLabel(label);
	}

	public void setValues(List<float[]> values) {
		this.values = values;
	}

	public String getLabel() {
		return label;
	}

	public List<float[]> getValues() {
		return values;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public float getValue(int index, int dim) {
		return values.get(index)[dim];
	}

	public void setValue(int index, int dim, float f) {
		values.get(index)[dim] = f;
	}

	public int length() {
		return values.size();
	}
}
