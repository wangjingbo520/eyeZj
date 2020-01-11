package com.l.eyescure.server.StorageManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {
	private SharedPreferences sp;
	private Editor editor;
	private final static String SP_NAME = "eys_data";
	private final static int MODE = 0x0001;

	public SharedPreferencesUtil(Context context) {
		if(sp == null)
		    sp = context.getSharedPreferences(SP_NAME, MODE);
		if(sp != null)
		  editor = sp.edit();
	}

	public boolean saveString(String key, String value) {
		if(editor!=null) {
			editor.putString(key, value);
			return editor.commit();
		}
		return  false;
	}

	public boolean saveInt(String key, int value) {
		if(editor!=null) {
			editor.putInt(key, value);
			return editor.commit();
		}
		return  false;
	}

	public String readString(String key) {
		String str = null;
		if(sp!=null)
		  str = sp.getString(key, null);
		return str;
	}

	public int readInt(String key) {
		int value = -1;
		if(sp!=null)
			value = sp.getInt(key, -1);
		return value;
	}
}
