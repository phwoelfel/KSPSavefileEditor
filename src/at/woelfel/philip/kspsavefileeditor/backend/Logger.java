package at.woelfel.philip.kspsavefileeditor.backend;

public class Logger {
	private static Logger mInstance;
	private boolean mIsEnabled;
	
	private Logger(){
		// TODO: log to file
	}
	
	public static Logger getInstance(){
		if(mInstance == null){
			mInstance = new Logger();
		}
		return mInstance;
	}
	
	public void log(Object str){
		if(mIsEnabled){
			System.out.println(str);
		}
	}

	public boolean isEnabled() {
		return mIsEnabled;
	}

	public void setEnabled(boolean mIsEnabled) {
		this.mIsEnabled = mIsEnabled;
	}
	
	
}
