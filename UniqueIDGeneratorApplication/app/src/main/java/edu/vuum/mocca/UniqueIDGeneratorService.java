package edu.vuum.mocca;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;

public class UniqueIDGeneratorService extends Service {
    private final String TAG = getClass().getName();

    final static String ID = "ID";

    private RequestHandler mRequestHandler = null;

    private Messenger mReqMessenger = null;

    @Override
    public void onCreate() {
	// The Messenger encapsulates the RequestHandler
	// used to handle request Messages sent from
	// UniqueIDGeneratorActivity.
	mRequestHandler = new RequestHandler();
	mReqMessenger = new Messenger(mRequestHandler);
    }

    public static Intent makeIntent(Context context) {
	return new Intent(context,
			  UniqueIDGeneratorService.class);
    }

    public static String uniqueID(Message replyMessage) {
	return replyMessage.getData().getString(ID);
    }

    private class RequestHandler extends Handler {
	private final int MAX_THREADS = 4;

	private ExecutorService mExecutor;

	private SharedPreferences uniqueIDs = null;

	public RequestHandler() {
	    uniqueIDs =
		PreferenceManager.getDefaultSharedPreferences
		(UniqueIDGeneratorService.this);

	    mExecutor =
		Executors.newFixedThreadPool(MAX_THREADS);
	}

	public void shutdown() {
	    mExecutor.shutdown();
	}

	private Message generateUniqueID() {
	    String uniqueID;

	    // Protected critical section to ensure the IDs are unique.
	    synchronized (this) {
		// This loop keeps generating a random UUID if it's
		// not unique (e.e., is not currently found in the
		// persistent collection of SharedPreferences). The
		// likelihood of a non-unique UUID is low, but we're
		// being extra paranoid for the sake of this example
		do {
		    uniqueID = UUID.randomUUID().toString();
		} while (uniqueIDs.getInt(uniqueID, 0) == 1);

		SharedPreferences.Editor editor = uniqueIDs.edit();
		editor.putInt(uniqueID, 1);
		editor.commit();
	    }

	    Message reply = Message.obtain();
	    Bundle data = new Bundle();
	    data.putString(ID, uniqueID);
	    reply.setData(data);
	    return reply;
	}

	public void handleMessage(Message request) {
	    final Messenger replyMessenger = request.replyTo;

	    mExecutor.execute(new Runnable() {
		    public void run() {
			Message reply =
			    generateUniqueID();

			try {
			    replyMessenger.send(reply);
			} catch (RemoteException e) {
			    e.printStackTrace();
			}
		    }
		});
	}
    }

    @Override
    public void onDestroy() {
	mRequestHandler.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
	return mReqMessenger.getBinder();
    }
}
