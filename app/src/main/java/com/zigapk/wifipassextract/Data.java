package com.zigapk.wifipassextract;
import android.content.*;
import android.preference.*;

public class Data {

	public static boolean isFirstTime(Context context){
        return  PreferenceManager.getDefaultSharedPreferences(context).getBoolean("first_time", true);
    }
	
	public static void setFirstTime(boolean value, Context context){
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("first_time", value);
		editor.commit();
	}
}
