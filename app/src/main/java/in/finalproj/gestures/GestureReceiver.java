package in.finalproj.gestures;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import in.finalproj.android.gesture.R;
import in.finalproj.android.gestureTrainer.GestureView;
import in.finalproj.gestures.classifier.Distribution;

public class GestureReceiver extends BroadcastReceiver {


    public GestureReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent("in.finalproj.gestures.GESTURE_RECOGNIZER");
        context.startService(serviceIntent);
    }
}
