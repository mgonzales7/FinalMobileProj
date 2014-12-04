package com.MobileProgramming.MusicPad;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SongFragment extends Fragment {
	private static final String TAG = "audioStuff";
    public static final String EXTRA_SONG_ID = "MusicPad.SONG_ID";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    public static final String PREFS_NAME = "SONG_APP";
    public static final String LIST_OF_SONGS = "List_of_Songs";
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int PLAYBACK_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
    private static final int PLAYBACK_SAMPLERATE = 44100;
    private AudioRecord recorder=null;
    private AudioTrack player=null;
    private Thread recordingThread = null;
    private Thread playbackThread = null;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    
    SaveDataList saveDataList = new SaveDataList();
    String mAudioPath;
    Song mSong;
    EditText mTitleField;
    Button mDateButton;
    Button mRecordButton;
    Button mStopButton;
    Button mPlayButton;
    

    public static SongFragment newInstance(UUID songId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SONG_ID, songId);

        SongFragment fragment = new SongFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("heyHere", "song fragment created");
        
        UUID songId = (UUID)getArguments().getSerializable(EXTRA_SONG_ID);
        mSong = SongLab.get(getActivity()).getSong(songId);
        
        setHasOptionsMenu(true);
        //Tell fragment manager that this fragment should receive a call to onOptionsItemSelected(...)
        // on behalf of the hosting activity when OS does callback on this method. 
        
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	//saveDataList.storeData(getActivity(), SongLab.get(getActivity()).getSongs(), PREFS_NAME, LIST_OF_SONGS);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	saveDataList.storeData(getActivity(), SongLab.get(getActivity()).getSongs(), PREFS_NAME, LIST_OF_SONGS);
    }
    public void updateDate() {
        mDateButton.setText(mSong.getDate().toString());
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_song, parent, false);
  
        //Enable the app icon as an up button, and display < 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        		if (NavUtils.getParentActivityName( getActivity( ) )  != null)  
        			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }   
        //enable the home-as-up button.
        //This enabled icon is treated as an existing option menu item. 
        
        //mAudioPath=mSong.getAudioPath(); Not needed
        mTitleField = (EditText)v.findViewById(R.id.song_title);
        mTitleField.setText(mSong.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mSong.setTitle(c.toString());
                mAudioPath=Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+c.toString()
                        + ".wav";
                Log.i(TAG, c.toString());
                mSong.setAudioPath(mAudioPath);
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });
        
        mDateButton = (Button)v.findViewById(R.id.song_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                    .newInstance(mSong.getDate());
                dialog.setTargetFragment(SongFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });
        
        mRecordButton = (Button)v.findViewById(R.id.recordButton);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				try {
					recordAudio(v);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
        mStopButton =(Button)v.findViewById(R.id.stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				stopClicked(v);
			}
		});
        mPlayButton =(Button)v.findViewById(R.id.playButton);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
        	
        	public void onClick(View v) {
        		try {
					playAudio(v);
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        });
       
        return v; 
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mSong.setDate(date);
            updateDate();
        }
    }

    //option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            
        	// We don't need to define or inflate the app icon menu item in an XML file. 
        //It comes with a ready-made resource ID: android.R.id.home
        		case android.R.id.home :
          	
        			if (NavUtils.getParentActivityName(getActivity())!=null )
        				NavUtils.navigateUpFromSameTask(getActivity());
                //Correspondingly, we need to add meta-data tag in AndroidManifest.xml to specify 
                // the parent activity of this fragment's hosting activities: CrimeActivity, CrimePagerActivity.
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } 
    }
    
    private byte[] short2byte(short[] sData) {
    	//converts audio from type short to type byte
    	int shortArrsize= sData.length;
    	byte[] bytes = new byte [shortArrsize *2];
    	for (int i = 0; i < shortArrsize; i++) {
    		bytes [i*2] = (byte) (sData[i] & 0x00FF);
    		bytes[(i*2)+1] = (byte) (sData[i] >>8);
    		sData[i] = 0;		
    	}
    	return bytes;
    }
    
    int BufferElements2Rec=1024;
    int BytesPerElement = 2;
    public void recordAudio (View view) throws IOException
    {
    			
    	recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS, 
    							   RECORDER_AUDIO_ENCODING, 16000 * 30);
    	recorder.startRecording();
    	isRecording=true;
    	recordingThread=new Thread(new Runnable() {
    		public void run(){
    			writeAudioDataToFile();
    		}
    	}, "AudioRecorder Thread");
    	recordingThread.start();
    }
    
    private void writeAudioDataToFile() {
    	//Writing to File Path
    	String filePath=mSong.getAudioPath();
    	short sData[] = new short[BufferElements2Rec];
    	
    	FileOutputStream os = null;
    	try {
    			os = new FileOutputStream(filePath);
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	}
    	
    	while (isRecording) {
    		//Method for mic to byte conversion
    		
    		recorder.read(sData, 0,BufferElements2Rec);
    		System.out.println("Short writing to file " + sData.toString());
    		try {
    			byte bData[] = short2byte(sData);
    			os.write(bData, 0, BufferElements2Rec * BytesPerElement);
    		} catch(IOException e) {
    			e.printStackTrace();
    		}
    	}
    	try {
    		os.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
   }
    
    public void stopClicked (View view)
    {
    	if(null !=recorder) {
    		isRecording = false;
    		recorder.stop();
    		recorder.release();
    		recorder = null;
    		recordingThread= null;
    	}
    }
    public void playAudio (View view) throws IOException{ 
   
    
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
 }

