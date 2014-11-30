package com.MobileProgramming.MusicPad;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Song implements Serializable{
    private UUID mId;
    private String mTitle;
    private String mAudioPath;
    private Date mDate;

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


}
