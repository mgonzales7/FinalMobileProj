package com.MobileProgramming.MusicPad;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class SongActivity extends SingleFragmentActivity {
	@Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID)getIntent()
            .getSerializableExtra(SongFragment.EXTRA_SONG_ID);
        return SongFragment.newInstance(crimeId);
    }
}
