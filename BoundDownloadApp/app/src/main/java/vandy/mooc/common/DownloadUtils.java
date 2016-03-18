package vandy.mooc.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import vandy.mooc.R;
import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

public class DownloadUtils {
    static final String TAG = DownloadUtils.class.getSimpleName();

    static final boolean DOWNLOAD_OFFLINE = false;

    static final int OFFLINE_TEST_IMAGE = R.raw.dougs;

    static final String OFFLINE_FILENAME = "dougs.jpg";
    
    public static Uri downloadFile(Context context,
				   Uri uri) {
	if (DOWNLOAD_OFFLINE) {
	    try (FileOutputStream out =
		     context.openFileOutput(OFFLINE_FILENAME, 0);
		 InputStream in =
		     context.getResources().openRawResource(OFFLINE_TEST_IMAGE)) {
		copy(in, out);
	    } catch (Exception e) {
		Log.e(TAG,
		      "Exception while downloading. Returning null.");
		Log.e(TAG,
		      e.toString());
		e.printStackTrace();
		return null;
	    }
	    return Uri.parse(context.getFilesDir().toString()
			     + File.separator
			     + OFFLINE_FILENAME);
	} else {
	    final File file = getTemporaryFile(context, uri.toString());
	    Log.d(TAG,
		  " downloading to "
		  + file);
	    try (final InputStream in = (InputStream)
		     new URL(uri.toString()).getContent();
		 final OutputStream os =
		     new FileOutputStream(file)) {
		copy(in, os);
	    } catch (Exception e) {
		Log.e(TAG,
		      "Exception while downloading. Returning null.");
		Log.e(TAG,
		      e.toString());
		e.printStackTrace();
		return null;
	    }
	    return Uri.parse(file.getAbsolutePath());
	}
    }

    static private File getTemporaryFile(Context context,
					 String url) {
	return context.getFileStreamPath(Base64.encodeToString(url.getBytes(),
							       Base64.NO_WRAP));
    }

    static public int copy(final InputStream in,
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
}
