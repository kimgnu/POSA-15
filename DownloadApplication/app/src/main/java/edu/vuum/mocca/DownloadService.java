package edu.vuum.mocca;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

public class DownloadService extends Service {
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;

    public void onCreate() {
	super.onCreate();
	HandlerThread thread =
	    new HandlerThread("DownloadService");
	thread.start();
	mServiceLooper = thread.getLooper();
	mServiceHandler =
	    new ServiceHandler(mServiceLooper);
    }

    public int onStartCommand(Intent intent,
			      int flags,
			      int startId) {
	Message message =
	    mServiceHandler.makeDownloadMessage(intent,
						startId);
	
	mServiceHandler.sendMessage(message);
	return Service.START_NOT_STICKY;
    }

    public static String getPathname(Message message) {
	Bundle data = message.getData();
	String pathname = data.getString("PATHNAME");
	if (message.arg1 != Activity.RESULT_OK
	    || pathname == null)
	    return null;
	else
	    return pathname;
    }

    public static Intent makeIntent(Context context,
				    Uri uri,
				    Handler downloadHandler) {
	Intent intent = new Intent(context,
				   DownloadService.class);
	intent.setData(uri);
	intent.putExtra("MESSENGER",
			new Messenger(downloadHandler));
	return intent;
    }

    private final class ServiceHandler extends Handler {
	public ServiceHandler(Looper looper) {
	    super(looper);
	}

	private Message makeDownloadMessage(Intent intent,
					    int startId) {
	    Message message = Message.obtain();
	    message.obj = intent;
	    message.arg1 = startId;
	    return message;
	}

	public void handleMessage(Message message) {
	    downloadImageAndReply((Intent) message.obj);
	    // Started service must stop self in a service context or
	    // be stopped by stopService(Intent) in the activity context
	    // to not run forever
	    stopSelf(message.arg1);
	}
	
	private void downloadImageAndReply(Intent intent) {
	    String pathname = downloadImage(DownloadService.this,
					    intent.getData().toString());
	    
	    Messenger messenger = (Messenger)
		intent.getExtras().get("MESSENGER");
	    sendPath(messenger, pathname);
	}

	public String downloadImage(final Context context,
				    final String url) {
	    try {
		final File file = getTemporaryFile(context, url);
		Log.d(getClass().getName(), " downloading to " + file);
		final InputStream in = (InputStream)
		    new URL(url).getContent();
		final OutputStream out =
		    new FileOutputStream(file);

		copy(in, out);
		in.close();
		out.close();
		return file.getAbsolutePath();
	    } catch (Exception e) {
		Log.e(getClass().getName(),
		      "Exception while downloading. Returning null.");
		Log.e(getClass().getName(),
		      e.toString());
		e.printStackTrace();
		return null;
	    }
	}
	
	private File getTemporaryFile(final Context context,
				      final String url) throws IOException {
	    return context.getFileStreamPath(Base64.encodeToString(url.getBytes(),
								   Base64.NO_WRAP)
					     + System.currentTimeMillis());
	}

	private int copy(final InputStream in,
			 final OutputStream out) throws IOException {
	    final int BUFFER_LENGTH = 1024;
	    final byte[] buffer = new byte[BUFFER_LENGTH];
	    int totalRead = 0;
	    int read = 0;

	    while ((read = in.read(buffer)) != -1) {
		out.write(buffer, 0, read);
		totalRead += read;
	    }
	    return totalRead;
	}

	private void sendPath(Messenger messenger,
			      String pathname) {
	    Message message = makeReplyMessage(pathname);
	    try {
		messenger.send(message);
	    } catch (RemoteException e) {
		Log.e(getClass().getName(),
		      "Exception while sending,",
		      e);
	    }
	}
	
	private Message makeReplyMessage(String pathname) {
	    Message message = Message.obtain();
	    message.arg1 = pathname == null
		? Activity.RESULT_CANCELED
		: Activity.RESULT_OK;

	    Bundle data = new Bundle();

	    data.putString("PATHNAME",
			   pathname);
	    message.setData(data);
	    return message;
	}
    }

    public void onDestroy() {
	mServiceLooper.quit();
    }

    public IBinder onBind(Intent arg0) {
	return null;
    }
}
