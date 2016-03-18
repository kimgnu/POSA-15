package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.MVP;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.DownloadImagePresenter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class DownloadImageActivity
    extends GenericActivity<MVP.RequiredViewOps,
				MVP.ProvidedPresenterOps,
				DownloadImagePresenter>
    implements MVP.RequiredViewOps {

    private EditText mUrlEditText;
    private String mDefaultUrl =
	"http://www.dre.vanderbilt.edu/~schmidt/ka.png";
    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.download_image_activity);

	mUrlEditText = (EditText) findViewById(R.id.url);
	mImageView = (ImageView) findViewById(R.id.imageView1);

	super.onCreate(DownloadImagePresenter.class, this);
    }

    public void downloadImage(View view) {
	Uri uri = Uri.parse(getUrlString());
	Utils.hideKeyboard(this, mUrlEditText.getWindowToken());

	switch(view.getId()) {
	case R.id.bound_sync_button:
	    if (getPresenter().downloadImageSync(uri) == false)
		Utils.showToast(this,
				"Download already in progress");
	    break;
	case R.id.bound_async_button:
	    if (getPresenter().downloadImageAsync(uri) == false)
		Utils.showToast(this,
				"Download already in progress");
	    break;
	}
    }

    public void resetImage(View view) {
	getPresenter().resetImage();
    }
    
    public void displayImage(final Bitmap image) {
	if (Utils.runningOnUiThread()) {
	    if (image == null)
		Utils.showToast(this,
				"image is corrupted,"
				+ " please check the requested URL.");
	    else
		mImageView.setImageBitmap(image);
	} else {
	    final Runnable displayRunnable = new Runnable() {
		    public void run() {
			displayImage(image);
		    }
		};
	    runOnUiThread(displayRunnable);
	}
    }
    
    private String getUrlString() {
	String s = mUrlEditText.getText().toString();
	if (s.equals(""))
	    s = mDefaultUrl;
	return s;
    }
}
