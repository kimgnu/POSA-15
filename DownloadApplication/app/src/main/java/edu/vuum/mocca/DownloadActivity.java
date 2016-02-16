package edu.vuum.mocca;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class DownloadActivity extends Activity {
    private EditText mUrlEditText;
    private ImageView mImageView;
    private String mDefaultUrl =
	"http://www.dre.vanderbilt.edu/~schmidt/ka.png";
    private ProgressDialog mProgressDialog;
    Handler mDownloadHandler = null;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	mUrlEditText = (EditText) findViewById(R.id.mUrlEditText);
	mImageView = (ImageView) findViewById(R.id.mImageView);

	mDownloadHandler = new DownloadHandler(this);
    }

    void showErrorToast(String errorString) {
	Toast.makeText(this, errorString,
		       Toast.LENGTH_LONG).show();
    }

    void displayImage(Bitmap image) {
	if (mImageView == null)
	    showErrorToast("Problem with Application,"
			   + " please contact the Developer.");
	else if (image != null)
	    mImageView.setImageBitmap(image);
	else
	    showErrorToast("image is corrupted,"
			   + " please check the requested URL.");
    }

    public void resetImage(View view) {
	mImageView.setImageResource(R.drawable.default_image);
    }

    public void downloadImage(View view) {
	String url = getUrlString();
	Log.e(DownloadActivity.class.getSimpleName(),
	      "Downloading " + url);
	hideKeyboard();

	showDialog("downloading via startService()");

	Intent intent = DownloadService.makeIntent(this,
						   Uri.parse(url),
						   mDownloadHandler);
	startService(intent);
    }

    private static class DownloadHandler extends Handler {
	private WeakReference<DownloadActivity> mActivity;

	public DownloadHandler(DownloadActivity activity) {
	    mActivity = new WeakReference<DownloadActivity>(activity);
	}
	
	public void handleMessage(Message message) {
	    DownloadActivity activity = mActivity.get();
	    if (activity == null) return;
	    String pathname = DownloadService.getPathname(message);
	    if (pathname == null)
		activity.showDialog("failed download");
	    activity.dismissDialog();
	    activity.displayImage(BitmapFactory.decodeFile(pathname));
	}
    }

    public void showDialog(String message) {
	mProgressDialog =
	    ProgressDialog.show(this,
				"Download",
				message,
				true);
    }

    public void dismissDialog() {
	if (mProgressDialog != null)
	    mProgressDialog.dismiss();
    }

    private void hideKeyboard() {
	InputMethodManager mgr =
	    (InputMethodManager) getSystemService
	    (Context.INPUT_METHOD_SERVICE);
	mgr.hideSoftInputFromWindow(mUrlEditText.getWindowToken(),
				    0);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.options, menu);
	return true;
    }

    String getUrlString() {
	String s =mUrlEditText.getText().toString();
	if (s.equals(""))
	    s = mDefaultUrl;
	return s;
    }
}
