package at.woelfel.philip.kspsavefileeditor.backend;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {
	protected static Preferences prefs;
	protected static final String prefName="/at/woelfel/philip/ksp/savefileeditor";
	
	public static final String PREF_KSP_DIR="ksp_dir";
	
	protected static void checkPrefs(){
		if(prefs == null){
			prefs = Preferences.userRoot().node(Settings.prefName);
		}
	}
	
	
	public static String getString(String key, String defaultValue){
		checkPrefs();
		return prefs.get(key, defaultValue);
	}
	
	public static boolean setString(String key, String value){
		checkPrefs();
		prefs.put(key, value);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			return false;
		}
		return true;
	}
}
