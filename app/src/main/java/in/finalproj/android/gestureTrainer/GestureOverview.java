/*
 * GestureOverview.java
 */

package in.finalproj.android.gestureTrainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import in.finalproj.android.gesture.R;
import in.finalproj.gestures.Gesture;
import in.finalproj.gestures.IGestureRecognitionService;

public class GestureOverview extends ListActivity {

	String trainingSet="default";
	private IGestureRecognitionService recognitionService;
	List<Gesture> trainingSetData = Collections.emptyList();
	Context context;
	Gesture signal;
	
	@SuppressWarnings("unchecked")
	public void loadTrainingSet(String trainingSetName) {
		context = GestureOverview.this;
		Toast.makeText(GestureOverview.this, String.format(trainingSetName), Toast.LENGTH_SHORT).show();
			FileInputStream input;
			ObjectInputStream o;
			try {
				input = new FileInputStream(new File(context.getExternalFilesDir(null), trainingSetName + ".gst"));
				o = new ObjectInputStream(input);
				trainingSetData = (ArrayList<Gesture>) o.readObject();
				try {
					o.close();
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				trainingSetData = new ArrayList<Gesture>();
			}
		
	}
	private final ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			recognitionService = IGestureRecognitionService.Stub.asInterface(service);
			try {
				context = GestureOverview.this;
				List<String> items = recognitionService.getGestureList(trainingSet);
				setListAdapter(new ArrayAdapter<String>(GestureOverview.this, R.layout.gesture_item, items));
				loadTrainingSet(trainingSet);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
			registerForContextMenu(lv);

			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// When clicked, show a toast with the TextView text
					//Toast.makeText(getApplicationContext(), "" + position + ((TextView) view).getText(), Toast.LENGTH_LONG).show();
					signal = trainingSetData.get(position);
					Intent getureVIew = new Intent().setClass(getApplicationContext(), GestureView.class);
					getureVIew.putExtra("signal", signal);
					startActivity(getureVIew);
				}
			});
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			recognitionService = null;
		}
	};

	@Override
	protected void onResume() {
		trainingSet = getIntent().getExtras().get("trainingSetName").toString();
		Intent bindIntent = new Intent("in.finalproj.gestures.GESTURE_RECOGNIZER");
		bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);

		super.onResume();
	}

	@Override
	protected void onPause() {
		recognitionService = null;
		unbindService(serviceConnection);
		super.onPause();
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(getListAdapter().getItem(info.position).toString());
		String[] menuItems = { "Delete" };

		for (int i = 0; i < menuItems.length; i++) {
			menu.add(Menu.NONE, i, i, menuItems[i]);
		}

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		if (item.getItemId() == 0) {
			try {
				recognitionService.deleteGesture(trainingSet, getListAdapter().getItem(info.position).toString());
				List<String> items = recognitionService.getGestureList(trainingSet);
				setListAdapter(new ArrayAdapter<String>(GestureOverview.this, R.layout.gesture_item, items));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;

	}

}
