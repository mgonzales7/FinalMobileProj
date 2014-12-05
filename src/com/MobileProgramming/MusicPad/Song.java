package com.MobileProgramming.MusicPad;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Song implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1106210551407841813L;
	private UUID mId;
    private String mTitle;
    private String mAudioPath;
    private Date mDate;
    private boolean isChecked;

    public Song() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    @Override
   public String toString() {
        return mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

	public String getAudioPath() {
		return mAudioPath;
	}

	public void setAudioPath(String audioPath) {
		mAudioPath = audioPath;
	}
	
	public void setChecked(boolean set) {
		isChecked = set;
	}
	
	public boolean isChecked() {
		return isChecked;
	}

}
