package at.woelfel.philip.kspsavefileeditor.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import at.woelfel.philip.kspsavefileeditor.MainClass;

public class Parser {
	private ArrayList<String> mLines;
	private int mCurrentLine = 0;
	private int mLineCount;
	
	private Hashtable<String, ImageIcon> mImageCache;

	
	public Parser(String fileName){
		this(new File(fileName));
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
		mImageCache = new Hashtable<String, ImageIcon>();
	}
	
	public Node parse(boolean hasRootNode) throws Exception{
		Node n = null;
		if(hasRootNode){
			long startTime = System.currentTimeMillis();
			n = parseLines(null, 1);
			long endTime = System.currentTimeMillis();
			System.out.println("Processing time: "+(endTime-startTime));
		}
		else{
			//n = new Node("", null); // create a nameless root node
			n = parseLines(null, 3); // parse with parent node present --> mode 3
		}
		return n;
	}
	
	private synchronized Node parseLines(Node parentNode, int mode) throws Exception{
		Node currentNode = new Node("", parentNode);
		for (; mCurrentLine < mLineCount; mCurrentLine++) {
			
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
				line = line.substring(0, line.indexOf("//"));
				Logger.log("found comment! new line: " +line);
			}
			Logger.log("current Line(" +mCurrentLine +"): \"" +line +"\" mode: " +mode);
			if(mode == 1){ // node name
				if(isValidName(line)){
					currentNode.setNodeName(line);
					mode = 2;
					ImageIcon tmpIcon = MainClass.readImage("nodes/"+line.toLowerCase() +".png");
					if(tmpIcon != null){
						currentNode.setIcon(tmpIcon);
					}
					
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
					
					if("type".equals(key) && "Flag".equals(value)){
						currentNode.setIcon(MainClass.readImage("nodes/flag.png"));
						// will override vessel icon because it is set later
					}
					
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
		 Pattern pattern = Pattern.compile("[A-Za-z0-9_-]*");
		 Matcher matcher = pattern.matcher(line);
		 return matcher.matches();
	}
}
