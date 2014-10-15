package com.godfather.selfieshare.utils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
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
