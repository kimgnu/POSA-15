package course.examples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MusicActivity extends Activity {
    private final String TAG =
	MusicActivity.class.getSimpleName();

    private static String DEFAULT_SONG =
	"http://www.dre.vanderbilt.edu/~schmidt/braincandy.m4a";

    private EditText mUrlEditText;

    private Intent mMusicServiceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	mUrlEditText =
	    (EditText) findViewById(R.id.mUrlEditText);
    }

    public void playSong(View src) {
	mMusicServiceIntent =
	    MusicService.makeIntent(this,
				    getUrlString());
	startService(mMusicServiceIntent);
    }

    public void stopSong(View src) {
	if (mMusicServiceIntent != null) {
	    stopService(mMusicServiceIntent);
	    mMusicServiceIntent = null;
	} else
	    showToast("no song is currently playing");
    }

    public String getUrlString() {
	String url =
	    mUrlEditText.getText().toString();
	if (url.equals(""))
	    url = DEFAULT_SONG;

	return url;
    }

    public void showToast(String toastString) {
	Toast.makeText(this,
		       toastString,
		       Toast.LENGTH_SHORT).show();
    }
}
