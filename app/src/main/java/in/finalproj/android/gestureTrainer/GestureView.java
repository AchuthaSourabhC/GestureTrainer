package in.finalproj.android.gestureTrainer;

import java.util.ArrayList;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import in.finalproj.android.gesture.R;
import in.finalproj.gestures.Gesture;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

public class GestureView extends Activity {
	
	LineChart mChart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesture_view);
		mChart = (LineChart) findViewById(R.id.gchart);
		Gesture signal = (Gesture)getIntent().getSerializableExtra("signal");
		if(signal == null){
			Toast.makeText(GestureView.this, String.format("Empty"), Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(GestureView.this, String.format("Full"), Toast.LENGTH_SHORT).show();
			setGraphData(signal);
			//toastGesture(signal);
		}
	}
	
	private void setGraphData(Gesture signal) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < signal.length(); i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
        ArrayList<Entry> yVals3 = new ArrayList<Entry>();
        for (int i = 0; i < signal.length(); i++) {

            yVals.add(new Entry(signal.getValue(i, 0), i));
            yVals2.add(new Entry(signal.getValue(i, 1), i));
            yVals3.add(new Entry(signal.getValue(i, 2), i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "x-axis");
        
        LineDataSet set2 = new LineDataSet(yVals2, "y-axis");
        LineDataSet set3 = new LineDataSet(yVals3, "z-axis");
        
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(2f);
        set1.setCircleSize(4f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        
        
        set2.setColor(ColorTemplate.PASTEL_COLORS[1]);
        set2.setCircleColor(ColorTemplate.PASTEL_COLORS[1]);
        set2.setLineWidth(2f);
        set2.setCircleSize(4f);
        set2.setFillAlpha(65);
        set2.setFillColor(ColorTemplate.PASTEL_COLORS[1]);
        
        set3.setColor(ColorTemplate.PASTEL_COLORS[0]);
        set3.setCircleColor(ColorTemplate.PASTEL_COLORS[0]);   
        set3.setLineWidth(2f);
        set3.setCircleSize(4f);
        set3.setFillAlpha(65);
        set3.setFillColor(ColorTemplate.PASTEL_COLORS[0]);
       

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        
        dataSets.add(set1); // add the datasets
        dataSets.add(set2);
        dataSets.add(set3);
        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        mChart.setYRange(-20, 20, false);
        // set data
        mChart.setData(data);
    }
	
	private void toastGesture(Gesture signal){
		String s = "";
		if(! signal.getValues().isEmpty()){
			Toast.makeText(GestureView.this, String.format("Singal size "+ signal.length()), Toast.LENGTH_SHORT).show();
			
			for (int i = 0; i < signal.length(); i++) {
				s = s + i + ", " + signal.getValue(i, 0) + "\n";
			}
			Toast.makeText(GestureView.this, String.format(s), Toast.LENGTH_LONG).show();
					
		}
	}
	
	

}
