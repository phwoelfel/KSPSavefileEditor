package at.woelfel.philip.kspsavefileeditor.backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Logger {
	private static Logger mInstance;
	private static boolean mIsEnabled = false;
	private static boolean mFileEnabled = false;
	
	private static BufferedWriter mFileWriter;
	private static File mLogFile = new File("log.txt");
	
	private Logger(){
	}
	
	public static void log(Object str){
		if(mIsEnabled){
			System.out.println(str);
		}
		if(mFileEnabled){
			if(mFileWriter == null){
				try {
					mFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mLogFile)));
				} catch (FileNotFoundException e) {
					System.err.println("Couldn't create logfile!" +e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
			try {
				mFileWriter.write(""+str);
				mFileWriter.newLine();
				mFileWriter.flush();
			} catch (IOException e) {
				System.err.println("Error writing to logfile!" +e.getLocalizedMessage());
				e.printStackTrace();
				
			}
		}
	}

	public static boolean isEnabled() {
		return mIsEnabled;
	}

	public static void setEnabled(boolean isEnabled) {
		mIsEnabled = isEnabled;
	}
	
	public static boolean isFileEnabled() {
		return mFileEnabled;
	}

	public static void setFileEnabled(boolean fileEnabled) {
		mFileEnabled = fileEnabled;
	}
	
	public static void setLogFile(File file) throws FileNotFoundException{
		System.out.println("setting logfile: "+file);
		if(file != null){
			mLogFile = file;
			if(mFileWriter != null){
				try {
					mFileWriter.close();
				} catch (IOException e) {
					System.err.println("Error closing old logfile!\n" +e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
			if(mLogFile.isDirectory()){
				System.out.println("logfile is dir");
				mLogFile = new File(mLogFile.getAbsolutePath()+"/log.txt");
				System.out.println("folder selected, adding log.txt");
			}
			mFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mLogFile)));
			System.out.println("created writer");
		}
	}
	
}
