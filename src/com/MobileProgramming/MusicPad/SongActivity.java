package com.MobileProgramming.MusicPad;

import java.util.UUID;

import android.support.v4.app.Fragment;
import android.widget.MediaController.MediaPlayerControl;

public class SongActivity extends SingleFragmentActivity implements MediaPlayerControl {
	@Override
    protected Fragment createFragment() {
        UUID songId = (UUID)getIntent()
            .getSerializableExtra(SongFragment.EXTRA_SONG_ID);
        return SongFragment.newInstance(songId);
    }
	
	
	

	public void start() {
		// TODO Auto-generated method stub
		
	}

	public void pause() {
		// TODO Auto-generated method stub
		
	}

	public int getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void seekTo(int pos) {
		// TODO Auto-generated method stub
		
	}

	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean canPause() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}
}
