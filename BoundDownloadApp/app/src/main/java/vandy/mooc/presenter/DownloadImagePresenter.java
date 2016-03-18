package vandy.mooc.presenter;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import vandy.mooc.R;
import vandy.mooc.common.GenericPresenter;
import vandy.mooc.model.DownloadImageModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class DownloadImagePresenter
    extends GenericPresenter<MVP.RequiredPresenterOps,
			     MVP.ProvidedModelOps,
			     DownloadImageModel>
    implements MVP.ProvidedPresenterOps,
	       MVP.RequiredPresenterOps {
    private final static String TAG =
	DownloadImagePresenter.class.getSimpleName();

    private WeakReference<MVP.RequiredViewOps> mView;

    private boolean mCallInProgress;

    private Bitmap mCurrentImage = null;

    private Bitmap mDefaultImage = null;

    @Override
    public void onCreate(MVP.RequiredViewOps view) {
	mView = new WeakReference<>(view);

	final InputStream is = (InputStream)
	    mView.get()
	    .getActivityContext()
	    .getResources()
	    .openRawResource(R.drawable.default_image);

	mDefaultImage = BitmapFactory.decodeStream(is);

	super.onCreate(DownloadImageModel.class, this);
    }

    @Override
    public void onConfigurationChange(MVP.RequiredViewOps view) {
	mView = new WeakReference<>(view);
	mView.get().displayImage(mCurrentImage);
    }

    @Override
    public void onDestroy(boolean isChangingConfigurations) {
	getModel().onDestroy(isChangingConfigurations);
    }

    public boolean downloadImageAsync(Uri uri) {
	if (mCallInProgress)
	    return false;
	else {
	    mCallInProgress = true;
	    getModel().downloadImageAsync(uri);
	    mCallInProgress = false;
	    return true;
	}
    }

    public boolean downloadImageSync(Uri uri) {
	if (mCallInProgress)
	    return false;
	else {
	    mCallInProgress = true;
	    getModel().downloadImageSync(uri);
	    mCallInProgress = false;
	    return true;
	}
    }
    
    public void resetImage() {
	mCurrentImage = mDefaultImage;
	mView.get().displayImage(mDefaultImage);
    }
    
    public void displayImage(Uri pathToImageFile) {
	mCurrentImage =
	    BitmapFactory.decodeFile(pathToImageFile.toString());

	mView.get().displayImage(mCurrentImage);
    }

    @Override
    public Context getActivityContext() {
	return mView.get().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
	return mView.get().getApplicationContext();
    }
}
