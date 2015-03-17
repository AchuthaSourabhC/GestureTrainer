/*
 * GestureTrainer.java
 */
package in.finalproj.android.gestureTrainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import in.finalproj.android.gesture.R;
import in.finalproj.gestures.Gesture;
import in.finalproj.gestures.IGestureRecognitionListener;
import in.finalproj.gestures.IGestureRecognitionService;
import in.finalproj.gestures.classifier.Distribution;

public class GestureTrainer extends Activity implements TextToSpeech.OnInitListener {

	IGestureRecognitionService recognitionService;
	String activeTrainingSet, sText;
	List<Gesture> trainingSet = Collections.emptyList();
	List<Gesture> trainingSetData = Collections.emptyList();
	Gesture signal;
	Context context ;
	GraphView graph;
	TextView datatv;
	LineChart mChart;
    private TextToSpeech tts;
	
	@SuppressWarnings("unchecked")
	public void loadTrainingSet(String trainingSetName) {
		Toast.makeText(GestureTrainer.this, String.format(trainingSetName), Toast.LENGTH_SHORT).show();
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

    private void speakOut(String text) {


        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


        private final ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			recognitionService = IGestureRecognitionService.Stub.asInterface(service);
			
			try {
				recognitionService.startClassificationMode(activeTrainingSet);
				recognitionService.registerListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			recognitionService = null;
		}
	};

	IBinder gestureListenerStub = new IGestureRecognitionListener.Stub() {

		@Override
		public void onGestureLearned(String gestureName) throws RemoteException {
			
			Toast.makeText(GestureTrainer.this, String.format("Gesture %s learned", gestureName), Toast.LENGTH_SHORT).show();
            speakOut("Gesture "+ gestureName +" learned");
		}

		@Override
		public void onTrainingSetDeleted(String trainingSet) throws RemoteException {
			Toast.makeText(GestureTrainer.this, String.format("Training set %s deleted", trainingSet), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onGestureRecognized(final Distribution distribution) throws RemoteException {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					sText = distribution.getBestMatch();
					Toast.makeText(GestureTrainer.this, String.format("%s: %f", distribution.getBestMatch(), distribution.getBestDistance()), Toast.LENGTH_SHORT).show();
                    speakOut(sText);
				}
			});
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final TextView activeTrainingSetText = (TextView) findViewById(R.id.activeTrainingSet);
		final EditText trainingSetText = (EditText) findViewById(R.id.trainingSetName);
		final EditText editText = (EditText) findViewById(R.id.gestureName);
		activeTrainingSet = trainingSetText.getText().toString();
		Toast.makeText(GestureTrainer.this, String.format(activeTrainingSet), Toast.LENGTH_SHORT).show();
		final Button startTrainButton = (Button) findViewById(R.id.trainButton);
		final Button deleteTrainingSetButton = (Button) findViewById(R.id.deleteTrainingSetButton);
		final Button changeTrainingSetButton = (Button) findViewById(R.id.startNewSetButton);
		activeTrainingSet = "default";
        sText = activeTrainingSet;
        tts = new TextToSpeech(this, this);
		final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
		loadTrainingSet(activeTrainingSet);
		seekBar.setVisibility(View.INVISIBLE);
		seekBar.setMax(20);
		seekBar.setProgress(20);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				try {
					recognitionService.setThreshold(progress / 10.0f);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		startTrainButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (recognitionService != null) {
					try {
						if (!recognitionService.isLearning()) {
							startTrainButton.setText("Stop Training");
							editText.setEnabled(false);
							deleteTrainingSetButton.setEnabled(false);
							changeTrainingSetButton.setEnabled(false);
							trainingSetText.setEnabled(false);
							recognitionService.startLearnMode(activeTrainingSet, editText.getText().toString());
						} else {
							startTrainButton.setText("Start Training");
							editText.setEnabled(true);
							deleteTrainingSetButton.setEnabled(true);
							changeTrainingSetButton.setEnabled(true);
							trainingSetText.setEnabled(true);
							recognitionService.stopLearnMode();
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		changeTrainingSetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				activeTrainingSet = trainingSetText.getText().toString();
				activeTrainingSetText.setText(activeTrainingSet);

				if (recognitionService != null) {
					try {
						recognitionService.startClassificationMode(activeTrainingSet);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		deleteTrainingSetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(GestureTrainer.this);
				builder.setMessage("You really want to delete the training set?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (recognitionService != null) {
							try {
								recognitionService.deleteTrainingSet(activeTrainingSet);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				builder.create().show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit_gestures:
			Intent editGesturesIntent = new Intent().setClass(this, GestureOverview.class);
			editGesturesIntent.putExtra("trainingSetName", activeTrainingSet);
			startActivity(editGesturesIntent);
			return true;

		default:
			return false;
		}
	}

	@Override
	protected void onPause() {
		try {
			recognitionService.unregisterListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		recognitionService = null;
		unbindService(serviceConnection);
		super.onPause();
	}

	@Override
	protected void onResume() {
		Intent bindIntent = new Intent("in.finalproj.gestures.GESTURE_RECOGNIZER");
		bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		super.onResume();
	}


    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                speakOut(sText);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}