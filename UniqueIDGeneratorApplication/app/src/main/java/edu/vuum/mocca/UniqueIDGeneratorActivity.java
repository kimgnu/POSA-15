package edu.vuum.mocca;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class UniqueIDGeneratorActivity extends Activity {
    private final String TAG = getClass().getName();

    private TextView mOutput;

    private Messenger mReqMessengerRef = null;

    private Messenger mReplyMessenger =
	new Messenger(new ReplyHandler());

    class ReplyHandler extends Handler {
	public void handleMessage(Message reply) {
	    // Get the unique ID encapsulated in reply Message
	    String uniqueID =
		UniqueIDGeneratorService.uniqueID(reply);
	    Log.d(TAG, "received unique ID " + uniqueID);
	    mOutput.setText(uniqueID);
	}
    }

    private ServiceConnection mSvcConn = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className,
					   IBinder binder) {
		Log.d(TAG, "ComponentName: " + className);
		mReqMessengerRef = new Messenger(binder);
	    }

	    public void onServiceDisconnected(ComponentName className) {
		mReqMessengerRef = null;
	    }
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	mOutput = (TextView) findViewById(R.id.output);
    }

    public void generateUniqueID(View view) {
	Message request = Message.obtain();
	request.replyTo = mReplyMessenger;

	try {
	    if (mReqMessengerRef != null) {
		Log.d(TAG, "sending message");
		mReqMessengerRef.send(request);
	    }
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
    }

    @Override
    protected void onStart() {
	super.onStart();
	Log.d(TAG, "onStart()");
	Log.d(TAG, "calling bindService()");
	if (mReqMessengerRef == null)
	    bindService(UniqueIDGeneratorService.makeIntent(this),
			mSvcConn,
			Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
	unbindService(mSvcConn);
	super.onStop();
    }
}
					      
