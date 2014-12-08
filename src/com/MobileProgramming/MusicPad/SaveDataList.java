package com.MobileProgramming.MusicPad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveDataList {
	
	public SaveDataList() {
		super();
	}
	
	public void storeData(Context context, List<Song> currentList, String pref_name, String key){
		SharedPreferences settings;
		Editor editor;
		//Get contexts for edit
		settings = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
		editor = settings.edit();
		
		//serialize object, store in gson for easy 
		Gson gson = new Gson();
		String jsonList = gson.toJson(currentList);
		
		//set into editor and push w/key
		editor.putString(key, jsonList);
		editor.apply();
	}
	
	public ArrayList<Song> getSavedList(Context context, String pre_name, String key) {
		SharedPreferences settings;
		List<Song> listSongs;
		//get context
		settings = context.getSharedPreferences(pre_name, Context.MODE_PRIVATE);
		
		//key exists?
		if(settings.contains(key)){
			//deserialize and get saved list
			String jsonList = settings.getString(key, null);
			Gson gson = new Gson();
			Song[] songListItems = gson.fromJson(jsonList, Song[].class);
			listSongs = Arrays.asList(songListItems);
			listSongs = new ArrayList<Song>(listSongs);
		}else
			return null; //null if doesn't exist
		
		return (ArrayList<Song>) listSongs; //return list of songs
	}
}
