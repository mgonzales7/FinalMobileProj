package com.MobileProgramming.MusicPad;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
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
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    String mAudioPath;
    Song mSong;
    EditText mTitleField;
    Button mDateButton;
    Button mRecordButton;
    Button mStopButton;
    Button mPlayButton;
    /*MediaRecorder mediaRecorder = new MediaRecorder();


    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    mediaRecorder.setOutputFile(audioFilePath);*/

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
        
        UUID songId = (UUID)getArguments().getSerializable(EXTRA_SONG_ID);
        mSong = SongLab.get(getActivity()).getSong(songId);

        setHasOptionsMenu(true);
        //Tell fragment manager that this fragment should receive a call to onOptionsItemSelected(...)
        // on behalf of the hosting activity when OS does callback on this method. 
        
        
        
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
                        + ".3gp";
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
    
    public void recordAudio (View view) throws IOException
    {
    	// isRecording = true;
    	  // stopButton.setEnabled(true);
    	   //playButton.setEnabled(false);
    	  // recordButton.setEnabled(false);
    	   
       try {
         mMediaRecorder = new MediaRecorder();
         mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
         mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
         mMediaRecorder.setOutputFile(mSong.getAudioPath());
         mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
         mMediaRecorder.prepare();
       } catch (Exception e) {
    	   e.printStackTrace();
       }

       mMediaRecorder.start();			
    }
    
    public void stopClicked (View view)
    {
    		
    	//stopButton.setEnabled(false);
    	//playButton.setEnabled(true);
    		
        //if (isRecording)
    	{	
    		//recordButton.setEnabled(false);
    		mMediaRecorder.stop();
    		mMediaRecorder.release();
    		mMediaRecorder = null;
    		//isRecording = false;
    	}// else {
    	//	mediaPlayer.release();
    	//       mediaPlayer = null;
        //	recordButton.setEnabled(true);
        //   }
    }
    public void playAudio (View view) throws IOException
    {
    //	playButton.setEnabled(false);
    //	recordButton.setEnabled(false);
    //	stopButton.setEnabled(true);

    	mMediaPlayer = new MediaPlayer();
    	mMediaPlayer.setDataSource(mSong.getAudioPath());
    	mMediaPlayer.prepare();
    	mMediaPlayer.start();
    }
 }

