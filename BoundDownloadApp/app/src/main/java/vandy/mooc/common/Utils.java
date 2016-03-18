package vandy.mooc.common;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Utils {
    private static final String TAG =
	Utils.class.getCanonicalName();

    public static String uppercaseInput(Context context,
					String input,
					boolean showToast) {
	if (input.isEmpty()) {
	    if (showToast)
		Utils.showToast(context,
				"no input provided");
	    return null;
	} else
	    return input.toUpperCase(Locale.ENGLISH);
    }

    public static void showToast(Context context,
				 String message) {
	Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void hideKeyboard(Activity activity,
				    IBinder windowToken) {
	InputMethodManager mgr =
	    (InputMethodManager) activity.getSystemService
	    (Context.INPUT_METHOD_SERVICE);
	mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public static void setActivityResult(Activity activity,
					 Uri pathToContent,
					 String failureReason) {
	if (pathToContent == null)
	    activity.setResult
		(Activity.RESULT_CANCELED,
		 new Intent("").putExtra("reason",
					 failureReason));
	else
	    activity.setResult
		(Activity.RESULT_OK,
		 new Intent("", pathToContent));
    }

    public static void setActivityResult(Activity activity,
					 int resultCode,
					 String failureReason) {
	if (resultCode ==Activity.RESULT_CANCELED)
	    activity.setResult
		(Activity.RESULT_CANCELED,
		 new Intent("").putExtra("reason",
					 failureReason));
	else
	    activity.setResult
		(Activity.RESULT_OK);
    }

    private Utils() {
	throw new AssertionError();
    }

    public static boolean runningOnUiThread() {
	return Thread.currentThread() == Looper.getMainLooper().getThread();
    }
}
    
