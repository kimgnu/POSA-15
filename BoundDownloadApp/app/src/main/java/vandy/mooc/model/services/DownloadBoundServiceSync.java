package vandy.mooc.model.services;

import vandy.mooc.common.DownloadUtils;
import vandy.mooc.common.LifecycleLoggingService;
import vandy.mooc.model.DownloadCall;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

public class DownloadBoundServiceSync
    extends LifecycleLoggingService {

    public static Intent makeIntent(Context context) {
	return new Intent(context,
			  DownloadBoundServiceSync.class);
    }

    DownloadCall.Stub mDownloadCallImpl =
	new DownloadCall.Stub() {
	    @Override
	    public Uri downloadImage(Uri uri) throws RemoteException {
		return DownloadUtils.downloadFile
		    (DownloadBoundServiceSync.this, uri);
	    }
	};

    @Override
    public IBinder onBind(Intent intent) {
	return mDownloadCallImpl;
    }
}
