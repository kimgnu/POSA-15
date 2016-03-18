package vandy.mooc.model.services;

import vandy.mooc.common.DownloadUtils;
import vandy.mooc.common.LifecycleLoggingService;
import vandy.mooc.model.DownloadRequest;
import vandy.mooc.model.DownloadResults;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

public class DownloadBoundServiceAsync
    extends LifecycleLoggingService {

    public static Intent makeIntent(Context context) {
	return new Intent(context,
			  DownloadBoundServiceAsync.class);
    }

    DownloadRequest.Stub mDownloadRequestImpl =
	new DownloadRequest.Stub() {
	    @Override
	    public void downloadImage(Uri uri,
				      DownloadResults results)
		throws RemoteException {
		results.sendPath(DownloadUtils.downloadFile
				 (DownloadBoundServiceAsync.this,
				  uri));
	    }
	};

    @Override
    public IBinder onBind(Intent intent) {
	return mDownloadRequestImpl;
    }
}
		       
