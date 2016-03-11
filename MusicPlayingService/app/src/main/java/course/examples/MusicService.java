package course.examples;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

public class MusicService extends Service
    implements MediaPlayer.OnPreparedListener {

    private String TAG = getClass().getSimpleName();

    private static String ACTION_PLAY =
	"course.examples.action.PLAY";

    private static boolean mSongPlaying;

    private MediaPlayer mPlayer;

    public static Intent makeIntent(final Context context,
				    String songURL) {
	return new Intent(ACTION_PLAY,
			  Uri.parse(songURL),
			  context,
			  MusicService.class);
    }

    @Override
    public void onCreate() {
	Log.i(TAG, "onCreate() entered");
	super.onCreate();

	mPlayer = new MediaPlayer();
	mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public int onStartCommand (Intent intent,
			       int flags,
			       int startid) {
	String songURL = intent.getDataString();
	Log.i(TAG, "onStartCommand() entered with song URL "
	      + songURL);

	if (mSongPlaying)
	    stopSong();

	try {
	    mPlayer.setDataSource(songURL);
	    mPlayer.setOnPreparedListener(this);
	    mPlayer.prepareAsync(); // doesn't block UI
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return START_NOT_STICKY;
    }

    public void onPrepared(MediaPlayer player) {
	Log.i(TAG, "onPrepared() entered");
	// Just play the song once, rather than have it loop endlessly
	player.setLooping(false);
	mSongPlaying = true;
	player.start();
    }

    @Override
    public void onDestroy() {
	Log.i(TAG, "onDestroy() entered");

        stopSong();
	super.onDestroy();
    }

    private void stopSong() {
	Log.i(TAG, "stopSong() entered");
	mPlayer.stop();
	mPlayer.reset();
	mSongPlaying = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }
}
