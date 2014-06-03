package at.woelfel.philip.kspsavefileeditor.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	private ArrayList<String> mLines;
	private int mCurrentLine = 0;
	
	private Logger mLogger;
	
	public Parser(String fileName){
		this(new File(fileName));
	}
	
	public Parser(File file) {
		mLogger = Logger.getInstance();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (br != null) {
			try {
				mLines = new ArrayList<String>();
				String line = "";
				while ((line = br.readLine()) != null) {
					mLines.add(line);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public Node parse(){
		long startTime = System.currentTimeMillis();
		Node n = parseLines(null, 1);
		long endTime = System.currentTimeMillis();
		System.out.println("Processing time: "+(endTime-startTime));
		return n;
	}
	
	private synchronized Node parseLines(Node parentNode, int mode){
		Node currentNode = new Node("", parentNode);
		for (; mCurrentLine < mLines.size(); mCurrentLine++) {
			String line = mLines.get(mCurrentLine).trim();
			mLogger.log("current Line(" +mCurrentLine +"): \"" +line +"\" mode: " +mode);
			if(mode == 1){ // node name
				if(isValidName(line)){
					currentNode.setNodeName(line);
					mode = 2;
					mLogger.log("found node name, setting mode 2: " +line);
				}
				else{
					// TODO: throw exception
					System.err.println("didn't find valid name");
					break;
				}
			}
			else if(mode == 2){ // opening bracket
				if(line.contains("{")){
					mLogger.log("found opening bracket, setting mode 3");
					mode = 3;
				}
				else{
					// TODO: throw exception
					System.err.println("didn't find {");
					break;
				}
				
			}
			else if(mode == 3){ // entry, new node or closing bracket
				if(line.contains("=")){ // entry
					String[] kv = line.split("=");
					String key = "";
					String value = "";
					if(kv.length == 1){
						key = kv[0].trim(); // it is allowed to have "key =" without a value
					}
					else if(kv.length > 1){
						key = kv[0].trim(); 
						value = kv[1].trim();
					}
					else{
						// TODO: throw exception
						System.err.println("couldn't split entry");
						break;
					}
					Entry tmp = new Entry(currentNode, key, value);
					currentNode.addEntry(tmp);
					mLogger.log("found entry, adding to currentNode: " +tmp);
				}
				else if(line.contains("}")){
					mode = 3;
					if(parentNode != null){
						parentNode.addSubNode(currentNode);
					}
					mLogger.log("found closing bracket, adding to parent and returning: " +currentNode);
					return currentNode;
				}
				else if(isValidName(line)){
					mLogger.log("found valid name, parsing subnode: " +line +"\n");
					mode = 3;
					parseLines(currentNode, 1);
					// TODO: zeilen die zu subnode gehoeren ueberspringen --> offset/i als Integer
					
				}
				
			}
		}
		return null;
	}
	
	private boolean isValidName(String line){
		 Pattern pattern = Pattern.compile("[A-Za-z_-]*");
		 Matcher matcher = pattern.matcher(line);
		 return matcher.matches();
	}

}
