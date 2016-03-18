package vandy.mooc;

import vandy.mooc.common.ContextView;
import vandy.mooc.common.ModelOps;
import vandy.mooc.common.PresenterOps;
import android.graphics.Bitmap;
import android.net.Uri;

public interface MVP {
    // presenter to view
    // model can access view's context 
    public interface RequiredViewOps
	extends ContextView {

	void displayImage(Bitmap image);
    }

    // view to presenter
    public interface ProvidedPresenterOps
	extends PresenterOps<MVP.RequiredViewOps> {

	boolean downloadImageAsync(Uri uri);

	boolean downloadImageSync(Uri uri);

	void resetImage();
    }

    // model to presenter?
    // model can access view's context
    public interface RequiredPresenterOps
	extends ContextView {

	void displayImage(Uri pathToImageFile);
    }

    // presenter to model
    public interface ProvidedModelOps
	extends ModelOps<MVP.RequiredPresenterOps> {

	void downloadImageAsync(Uri uri);

	void downloadImageSync(Uri uri);
    }
}
