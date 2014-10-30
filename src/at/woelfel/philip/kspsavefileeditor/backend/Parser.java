package at.woelfel.philip.kspsavefileeditor.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.woelfel.philip.kspsavefileeditor.gui.ProgressScreen;
import at.woelfel.philip.tools.Logger;

public class Parser {
	private ArrayList<String> mLines;
	private int mCurrentLine = 0;
	private int mLineCount;
	
	/**
	 * Constructor with contents as a String and not the filename
	 * @param content content of a savefile
	 * @param isContent uselsess, just to differ from Parser(String) constructor
	 */
	public Parser(String content, boolean isContent) {
		if(content != null && content.length()>0){
			mLines = new ArrayList<String>(Arrays.asList(content.split("\\r?\\n")));
			mLineCount = mLines.size();
		}
	}
	
	public Parser(String filename){
		this(new File(filename));
	}
	
	public Parser(File file) {
		
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
		mLineCount = mLines.size();
	}
	
	public ArrayList<TreeBaseNode> parseClipBoard() throws Exception{
		/*
		 * difference to parse:
		 * 	doesn't need a parent node
		 * 	can have multiple nodes on root level
		 * 	can have entries without a parent node
		 * 
		 * only root level is different, if node is found on root level can be parsed using normal parseLines
		 */
		ArrayList<TreeBaseNode> data = new ArrayList<TreeBaseNode>();
		for (; mCurrentLine < mLineCount; mCurrentLine++) {
			
			if(mCurrentLine%1000==0){
				// update every 1000 lines
				int percent = (mCurrentLine*100)/mLineCount;
				ProgressScreen.updateProgressBar(percent);
			}
			
			String line = mLines.get(mCurrentLine).trim();
			if(line.length()==0){
				// empty line, skipping
				continue;
			}
			if(line.startsWith("//")){
				// comment, skipping
				continue;
			}
			if(line.contains("//")){
				// comment not at the beginning, cut comment
				line = line.substring(0, line.indexOf("//")).trim();
				Logger.log("found comment! new line: " +line);
			}
			Logger.log("current Line(" +mCurrentLine +"): \"" +line +"\"");
			
			// entry or valid name possible
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
					throw new Exception("Couldn't split Entry at line "+mCurrentLine +": " +line);
				}
				Entry tmp = new Entry(null, key, value);
				data.add(tmp);
				
				Logger.log("found entry, adding to data: " +tmp);
			}
			else if(isValidName(line)){
				Logger.log("found valid name, parsing subnode: " +line +"\n---------------------------------------------------------------------------------------------------------");
				data.add(parseLines(null, 1));
			}
			else{
				throw new Exception("Found invalid line at "+mCurrentLine +": " +line +"\nExpected an Entry or a valid Node name!");
			}
		}
		
		return data;
	}
	
	public Node parse(boolean hasRootNode) throws Exception{
		if(mLines != null && mLines.size()>0){
			
			Node n = null;
			ProgressScreen.updateProgressBar(0);
			
			long startTime = System.currentTimeMillis();
			if(hasRootNode){	
				n = parseLines(null, 1);
			}
			else{
				//n = new Node("", null); // create a nameless root node
				n = parseLines(null, 3); // parse with parent node present --> mode 3
			}
			long endTime = System.currentTimeMillis();
			Logger.logInfo("Processing time: "+(endTime-startTime));
	
			
			return n;
		}
		return null;
	}
	
	private Node parseLines(Node parentNode, int mode) throws Exception{
		Node currentNode = new Node("", parentNode);
		for (; mCurrentLine < mLineCount; mCurrentLine++) {
			if(mCurrentLine%1000==0){
				// update every 1000 lines
				int percent = (mCurrentLine*100)/mLineCount;
				ProgressScreen.updateProgressBar(percent);
			}
			
			String line = mLines.get(mCurrentLine).trim();
			if(line.length()==0){
				// empty line, skipping
				continue;
			}
			if(line.startsWith("//")){
				// comment, skipping
				continue;
			}
			if(line.contains("//")){
				// comment not at the beginning, cut comment
				line = line.substring(0, line.indexOf("//")).trim();
				Logger.log("found comment! new line: " +line);
			}
			Logger.log("current Line(" +mCurrentLine +"): \"" +line +"\" mode: " +mode);
			if(mode == 1){ // node name
				if(isValidName(line)){
					currentNode.setNodeName(line);
					mode = 2;
					Logger.log("found node name, setting mode 2: " +line);
				}
				else{
					throw new Exception("Didn't find valid name but expected one at line "+mCurrentLine);
				}
			}
			else if(mode == 2){ // opening bracket
				if(line.contains("{")){
					Logger.log("found opening bracket, setting mode 3");
					mode = 3;
				}
				else{
					throw new Exception("Didn't find { but expected one at line "+mCurrentLine);
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
						throw new Exception("Couldn't split Entry at line "+mCurrentLine +": " +line);
					}
					Entry tmp = new Entry(currentNode, key, value);
					currentNode.addEntry(tmp);
					
					Logger.log("found entry, adding to currentNode: " +tmp);
				}
				else if(line.contains("}")){
					mode = 3;
					if(parentNode != null){
						parentNode.addSubNode(currentNode);
					}
					Logger.log("found closing bracket, adding to parent and returning: " +currentNode +"\n---------------------------------------------------------------------------------------------------------");
					return currentNode;
				}
				else if(isValidName(line)){
					Logger.log("found valid name, parsing subnode: " +line +"\n---------------------------------------------------------------------------------------------------------");
					mode = 3;
					parseLines(currentNode, 1);
					
				}
				
			}
		}
		return currentNode;
	}
	
	private boolean isValidName(String line){
		 Pattern pattern = Pattern.compile("[/A-Za-z0-9_-]*");
		 Matcher matcher = pattern.matcher(line);
		 return matcher.matches();
	}
}
