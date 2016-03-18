package vandy.mooc.model;

import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
// import vandy.mooc.model.DownloadCall;
// import vandy.mooc.model.DownloadRequest;
// import vandy.mooc.model.DownloadResults;
import vandy.mooc.model.services.DownloadBoundServiceAsync;
import vandy.mooc.model.services.DownloadBoundServiceSync;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class DownloadImageModel
    implements MVP.ProvidedModelOps {

    protected final static String TAG =
	DownloadImageModel.class.getSimpleName();

    protected WeakReference<MVP.RequiredPresenterOps> mPresenter;

    DownloadCall mDownloadCall;

    DownloadRequest mDownloadRequest;

    ServiceConnection mServiceConnectionSync =
	new ServiceConnection() {
	    @Override
	    public void onServiceConnected(ComponentName name,
					   IBinder service) {
		Log.d(TAG, "ComponentName: " + name);
		mDownloadCall =
		    DownloadCall.Stub.asInterface(service);
	    }

	    @Override
	    public void onServiceDisconnected(ComponentName name) {
		mDownloadCall = null;
	    }
	};

    ServiceConnection mServiceConnectionAsync =
	new ServiceConnection() {
	    @Override
	    public void onServiceConnected(ComponentName name,
					   IBinder service) {
		mDownloadRequest =
		    DownloadRequest.Stub.asInterface(service);
	    }

	    @Override
	    public void onServiceDisconnected(ComponentName name) {
		mDownloadRequest = null;
	    }
	};

    DownloadResults.Stub mDownloadResults =
	new DownloadResults.Stub() {
	    @Override
	    public void sendPath(final Uri pathToImageFile)
		throws RemoteException {
		mPresenter.get().displayImage(pathToImageFile);
	    }
	};

    @Override
    public void onCreate(MVP.RequiredPresenterOps presenter) {
	mPresenter = new WeakReference<>(presenter);

	if (mDownloadCall == null) {
	    mPresenter.get()
		.getApplicationContext()
		.bindService(DownloadBoundServiceSync.makeIntent
			     (mPresenter.get().getActivityContext()),
			     mServiceConnectionSync,
			     Context.BIND_AUTO_CREATE);
	    Log.d(TAG,
		  "Calling bindService() on DownloadBoundServiceSync");
	}
	if (mDownloadRequest == null) {
	    mPresenter.get()
		.getApplicationContext()
		.bindService(DownloadBoundServiceAsync.makeIntent
			     (mPresenter.get().getActivityContext()),
			     mServiceConnectionAsync,
			     Context.BIND_AUTO_CREATE);
	    Log.d(TAG,
		  "Calling bindService() on DownloadBoundServiceAsync");
	}
    }

    @Override
    public void onDestroy(boolean isChangingConfigurations) {
	if (isChangingConfigurations)
	    Log.d(TAG,
		  "Simply changing configurations, no need to destroy the Service");
	else {
	    if (mDownloadCall != null)
		mPresenter.get()
		    .getApplicationContext()
		    .unbindService(mServiceConnectionSync);
	    if (mDownloadRequest != null)
		mPresenter.get()
		    .getApplicationContext()
		    .unbindService(mServiceConnectionAsync);
	}
    }

    public void downloadImageAsync(Uri uri) {
	if (mDownloadRequest != null) {
	    try {
		Log.d(TAG,
		      "Calling oneway DownloadServiceAsync.downloadImage()");
		mDownloadRequest.downloadImage(uri,
					       mDownloadResults);
	    } catch (RemoteException e) {
		e.printStackTrace();
	    }
	} else
	    Log.d(TAG,
		  "mDownloadRequest is null");
    }

    public void downloadImageSync(Uri uri) {
	if (mDownloadCall != null) {
	    Log.d(TAG,
		  "Calling twoway DownloadServiceSync.downloadImage()");
	    new AsyncTask<Uri, Void, Uri>() {
		@Override
		protected Uri doInBackground(Uri... params) {
		    try {
			return mDownloadCall.downloadImage(params[0]);
		    } catch (RemoteException e) {
			e.printStackTrace();
		    }
		    return null;
		}

		@Override
		protected void onPostExecute(Uri result) {
		    mPresenter.get().displayImage(result);
		}
	    }.execute(uri);
	} else
	    Log.d(TAG,
		  "mDownloadCall is null");
    }
}
		
