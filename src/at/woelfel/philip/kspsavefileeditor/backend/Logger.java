package at.woelfel.philip.kspsavefileeditor.backend;

public class Logger {
	private static Logger mInstance;
	private static boolean mIsEnabled;
	
	private Logger(){
		// TODO: log to file
	}
	
	public static void log(Object str){
		if(mIsEnabled){
			System.out.println(str);
		}
	}

	public static boolean isEnabled() {
		return mIsEnabled;
	}

	public static void setEnabled(boolean mIsEnabled) {
		mIsEnabled = mIsEnabled;
	}
	
	
}
