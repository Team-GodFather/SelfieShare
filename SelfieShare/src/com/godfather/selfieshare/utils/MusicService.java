package com.godfather.selfieshare.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import com.godfather.selfieshare.AppMain;
import com.godfather.selfieshare.R;
import com.godfather.selfieshare.activities.LoginActivity;

public class MusicService extends Service {
    private String contentMessage = null;
    private int notificationID = 1;
	private static MediaPlayer musicPlayer;
	private final IBinder mBinder = new LocalBinder();
	private MusicService musicService = this;

	public class LocalBinder extends Binder {
		public MusicService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return musicService;
		}
	}

	public void loadSong(int songId) {
		if (musicPlayer != null) {
			musicPlayer.reset();
			musicPlayer.release();
		}

		musicPlayer = MediaPlayer.create(musicService, songId);
	}

	public void playSong() {
		if (musicPlayer != null && !musicPlayer.isPlaying()) {
			musicPlayer.start();

            long when = System.currentTimeMillis();
            CharSequence contentTitle = getText(R.string.app_name);
            int icon = R.drawable.icon;

            Notification notification = new Notification(icon, contentTitle, when);
            notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE;

            Intent notificationIntent = new Intent(this, AppMain.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(this, contentTitle, contentMessage, contentIntent);

            startForeground(notificationID, notification);
		}
	}

    public void pauseSong() {
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pause();
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.musicService = null;
		musicPlayer = null;
	}
}
