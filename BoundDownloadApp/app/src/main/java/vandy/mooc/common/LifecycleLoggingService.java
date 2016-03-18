package vandy.mooc.common;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public abstract class LifecycleLoggingService extends Service {
    protected final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
	super.onCreate();
	Log.d(TAG, "onCreate() - service created anew");
    }

    @Override
    public int onStartCommand(Intent intent,
			      int flags,
			      int startId) {
	Log.d(TAG,
	      "onStartCommand() - intent received");
	return super.onStartCommand(intent,
				   flags,
				   startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
	Log.d(TAG,
	      "onBind() - client has invoked bindService()");
	return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
	Log.d(TAG,
	      "onUnbind() -client has invoked unbindService()");
	return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	Log.d(TAG,
	      "onDestroy() - service is being shut down");
    }
}
