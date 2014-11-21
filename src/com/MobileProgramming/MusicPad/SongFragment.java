package com.MobileProgramming.MusicPad;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class SongFragment extends Fragment {
    public static final String EXTRA_SONG_ID = "MusicPad.SONG_ID";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;

    Song mSong;
    EditText mTitleField;
    Button mDateButton;

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
        
        
        mTitleField = (EditText)v.findViewById(R.id.song_title);
        mTitleField.setText(mSong.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mSong.setTitle(c.toString());
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
}
