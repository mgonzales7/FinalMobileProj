package com.MobileProgramming.MusicPad;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class SongLab {
    private ArrayList<Song> mSongs;

    private static SongLab sSongLab;
   

    private SongLab(Context appContext) {
        mSongs = new ArrayList<Song>();
        //Initially, no data in this ArrayList yet. 
    }

    public static SongLab get(Context c) {
    	//static context of the class given
        if (sSongLab == null) {
            sSongLab = new SongLab(c.getApplicationContext());
        }
        return sSongLab;
    }

    public Song getSong(UUID id) {
        for (Song s : mSongs) {
            if (s.getId().equals(id))
                return s;
        }
        return null;
    }
    
    public Song getSong(int index) {
    	return mSongs.get(index);
    }
    
    public void addSong(Song s) {
        mSongs.add(s);
    }
    
    public void deleteSong(int position) {
        mSongs.remove(position);
    }
    
    public void deleteSong(Song s) {
        mSongs.remove(s);
    }

    public ArrayList<Song> getSongs() {
        return mSongs;
    }
    
    public void setSongs(ArrayList<Song> otherList) {
    	try {
    		mSongs = new ArrayList<Song>(otherList);
    	}catch(IndexOutOfBoundsException ex) {
    		Log.i("keyHere", "IOOB ex");
    	}
    }
}

