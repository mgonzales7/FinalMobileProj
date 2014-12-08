package com.MobileProgramming.MusicPad;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class MusicService extends Service {
	
	private AudioTrack player;
	private Song mSong;
	private int songPosn;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int PLAYBACK_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
    private static final int PLAYBACK_SAMPLERATE = 44100;
    private int BufferElements2Rec=1024;
    private int BytesPerElement = 2;
    private Thread playbackThread = null;
    private boolean isPlaying = false;
    private final IBinder musicBind = new MusicBinder();
    private File soundfile;
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			playAudio();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return START_STICKY;
	}
	public void setSong(Song  s){
		  mSong = s;
		  soundfile = new File(mSong.getAudioPath());
		}
	public class MusicBinder extends Binder {
		  MusicService getService() {
		    return MusicService.this;
		  }
	}
	@Override
	public IBinder onBind(Intent intent) {
		return musicBind;
	}
	@Override
	public boolean onUnbind(Intent intent){
	  player.stop();
	  player.release();
	  return false;
	}
	
	public void playAudio () throws IOException{ 
		   
	    
	    //	playButton.setEnabled(false);
	    //	recordButton.setEnabled(false);
	    //	stopButton.setEnabled(true);
	    	player= new AudioTrack(AudioManager.STREAM_MUSIC,PLAYBACK_SAMPLERATE,PLAYBACK_CHANNELS, 
	    						   RECORDER_AUDIO_ENCODING,BufferElements2Rec*BytesPerElement,
	    						   AudioTrack.MODE_STREAM);
	    	player.play();
	    	isPlaying=true;
	    	playbackThread=new Thread(new Runnable() {
	    		public void run(){
	    			retrieveAudio();
	    		}
	    	}, "Playback Thread");
	    	playbackThread.start();
	    	
	    }
	    private void retrieveAudio(){
	    	int bufferSize = BufferElements2Rec*BytesPerElement;
	    	 byte[] audiodata=new byte[bufferSize / 4];
	    	  try {
	    	    DataInputStream dis=new DataInputStream(new BufferedInputStream(new FileInputStream(mSong.getAudioPath())));
	    	    while (isPlaying && dis.available() > 0) {
	    	      int i=0;
	    	      while (dis.available() > 0 && i < audiodata.length) {
	    	        audiodata[i]=dis.readByte();
	    	        i++;
	    	      }
	    	      player.write(audiodata,0,audiodata.length);
	    	    }
	    	    dis.close();
	    	  }
	    	 catch (  Throwable t) {
	    	    Log.e("AudioTrack","Playback Failed");
	    	  }
	    }
	    public int getPosn(){
	    	  return player.getPlaybackHeadPosition();
	    }
	    	 
	    	public int getDur(){
	    	  return (int) soundfile.length();
	    	}
	    	 
	    	public boolean isPng(){
	    	  return isPlaying;
	    	}
	    	 
	    	public void pausePlayer(){
	    	  player.pause();
	    	}
	    	 
	    	public void seek(int posn){
	    	  player.setPlaybackHeadPosition(posn);
	    	}
	    	 
	    	public void go(){
	    	  player.play();
	    	}
	 }

